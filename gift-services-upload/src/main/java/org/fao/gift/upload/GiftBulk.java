package org.fao.gift.upload;

import org.fao.ess.uploader.core.dto.ChunkMetadata;
import org.fao.ess.uploader.core.dto.FileMetadata;
import org.fao.ess.uploader.core.metadata.MetadataStorage;
import org.fao.ess.uploader.core.process.PostUpload;
import org.fao.ess.uploader.core.process.ProcessInfo;
import org.fao.ess.uploader.core.storage.BinaryStorage;
import org.fao.fenix.commons.msd.dto.full.DSDDataset;
import org.fao.fenix.commons.msd.dto.full.MeContent;
import org.fao.fenix.commons.msd.dto.full.MeIdentification;
import org.fao.fenix.commons.msd.dto.full.OjPeriod;
import org.fao.fenix.commons.msd.dto.full.SeCoverage;
import org.fao.gift.upload.dto.Files;
import org.fao.gift.upload.impl.DataManager;
import org.fao.gift.upload.impl.FileManager;
import org.fao.gift.upload.impl.FoodGroups;
import org.fao.gift.upload.impl.MetadataManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.NotAcceptableException;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@ProcessInfo(context = "gift.bulk", name = "GiftBulk", priority = 1)
public class GiftBulk implements PostUpload {

    private static final Logger log = LoggerFactory.getLogger(GiftBulk.class);

    @Inject
    private FileManager fileManager;
    @Inject
    private DataManager dataManager;
    @Inject
    private FoodGroups foodGroups;
    @Inject
    private MetadataManager metadataManager;

    private static final String CODE_BOOK_NAME = "FAO-WHO_GIFT_Code_book_V.2017-10-25.xlsx";

    @Override
    public void chunkUploaded(ChunkMetadata metadata, MetadataStorage metadataStorage, BinaryStorage storage) throws Exception {
        //Nothing to do here
    }

    @Override
    public void fileUploaded(FileMetadata metadata, MetadataStorage metadataStorage, BinaryStorage storage, Map<String, Object> processingParams) throws Exception {
        String survey = (String) processingParams.get("source");
        if (survey == null)
            throw new BadRequestException("Source is undefined");
        mainLogic(survey, storage.readFile(metadata, null));
    }

    public void mainLogic(String surveyCode, InputStream zipFileInput) throws Exception {
        log.info("Processing {}", surveyCode);

        //Retrieve database connection
        Connection connection = dataManager.getConnection();

        //Create temporary folder with zip file content
        File tmpFolder = fileManager.createTmpFolder();
        try {
            File file = fileManager.saveFile(tmpFolder, "survey_" + surveyCode + ".zip", zipFileInput);

            //Unzip file in the just created folder
            Map<Files, File> recognizedFilesMap = fileManager.unzip(tmpFolder, new FileInputStream(file));

            //Check all needed files are present
            if (recognizedFilesMap.size() != Files.values().length)
                throw new NotAcceptableException("Some CSV file is missing");

            //Clean existing tmp data
            dataManager.cleanTmpData(connection);

            //Upload food groups data
            foodGroups.fillFoodGroupsTable(connection);

            //Upload data into database stage area
            dataManager.uploadCSV(recognizedFilesMap.get(Files.subject), "STAGE.SUBJECT_RAW", connection);
            dataManager.uploadCSV(recognizedFilesMap.get(Files.consumption), "STAGE.CONSUMPTION_RAW", connection);

            //Validate uploaded temporary data
            dataManager.validateSurveyData(connection);

            //Publish temporary data
            dataManager.publishData(connection, surveyCode);

            // Prepare a download bundle with: subject_user, consumption_user and code book files
            File downloadBundle = new File(tmpFolder, surveyCode + "_bundle.zip");
            InputStream codeBookStream = this.getClass().getResourceAsStream("/gift/data/" + CODE_BOOK_NAME);

            Map<String, InputStream> fileStreamsToInclude = new HashMap<>();
            fileStreamsToInclude.put(CODE_BOOK_NAME, codeBookStream);
            Map<String, File> filesToInclude = new HashMap<>();
            filesToInclude.put(Files.subject.getFileName(), recognizedFilesMap.get(Files.subject_user)); // Files for user must be renamed
            filesToInclude.put(Files.consumption.getFileName(), recognizedFilesMap.get(Files.consumption_user));

            FileManager.buildZip(downloadBundle, fileStreamsToInclude, filesToInclude);
            log.info("Built zip bundle {}", downloadBundle);
            fileManager.publishSurveyFile(downloadBundle, surveyCode);

            //Update metadata
            metadataManager.updateSurveyMetadata(surveyCode);
            metadataManager.updateProcessingDatasetsMetadata(surveyCode);

            //Commit database changes
            connection.commit();

            //Start D3S data fetching
            metadataManager.fetchProcessingDatasetsMetadata(surveyCode);

        } catch (Exception ex) {
            log.error("", ex);
            connection.rollback();
            throw ex;

        } finally {
            fileManager.removeTmpFolder(tmpFolder);
            connection.close();
        }
    }

    private Integer getSurveyYear(MeIdentification<DSDDataset> metadata) {
        MeContent meContent = metadata != null ? metadata.getMeContent() : null;
        SeCoverage seCoverage = meContent != null ? meContent.getSeCoverage() : null;
        OjPeriod ojPeriod = seCoverage != null ? seCoverage.getCoverageTime() : null;
        Date from = ojPeriod != null ? ojPeriod.getFrom() : null;

        if (from != null) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(from);
            return calendar.get(Calendar.YEAR);

        } else
            return null;
    }
}