package org.fao.gift.upload;

import org.fao.ess.uploader.core.dto.ChunkMetadata;
import org.fao.ess.uploader.core.dto.FileMetadata;
import org.fao.ess.uploader.core.metadata.MetadataStorage;
import org.fao.ess.uploader.core.process.PostUpload;
import org.fao.ess.uploader.core.process.ProcessInfo;
import org.fao.ess.uploader.core.storage.BinaryStorage;
import org.fao.fenix.commons.msd.dto.full.*;
import org.fao.gift.upload.dto.Files;
import org.fao.gift.upload.impl.DataManager;
import org.fao.gift.upload.impl.FileManager;
import org.fao.gift.upload.impl.FoodGroups;
import org.fao.gift.upload.impl.MetadataManager;

import javax.inject.Inject;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.NotAcceptableException;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

@ProcessInfo(context = "gift.bulk", name = "GiftBulk", priority = 1)
public class GiftBulk implements PostUpload {
    @Inject private FileManager fileManager;
    @Inject private DataManager dataManager;
    @Inject private FoodGroups foodGroups;
    @Inject private MetadataManager metadataManager;


    @Override
    public void chunkUploaded(ChunkMetadata metadata, MetadataStorage metadataStorage, BinaryStorage storage) throws Exception {
        //Nothing to do here
    }

    @Override
    public void fileUploaded(FileMetadata metadata, MetadataStorage metadataStorage, BinaryStorage storage, Map<String, Object> processingParams) throws Exception {
        String survey = (String)processingParams.get("source");
        if (survey==null)
            throw new BadRequestException("Source is undefined");
        mainLogic(survey, storage.readFile(metadata, null));
    }

    public void mainLogic(String surveyCode, InputStream zipFileInput) throws Exception {
        //Retrieve database connection
        Connection connection = dataManager.getConnection();
        //Create temporary folder with zip file content
        File tmpFolder = fileManager.createTmpFolder();
        try {
            File file = fileManager.saveFile(tmpFolder, "survey_"+surveyCode+".zip", zipFileInput);
            //Unzip file into newly created folder
            Map<Files, File> recognizedFilesMap = fileManager.unzip(tmpFolder, new FileInputStream(file));
            //Check all needed files are present
            if (recognizedFilesMap.size()!=Files.values().length)
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
            //Transfer source file for bulk download
            //fileManager.publishSurveyFile(file, surveyCode); //original
            fileManager.publishSurveyFile(this.getClass().getResourceAsStream("/gift/data/emptySurvey.zip"), surveyCode); //temporary
            //Update metadata
            metadataManager.updateSurveyMetadata(surveyCode);
            metadataManager.updateProcessingDatasetsMetadata(surveyCode);
            //Commit database changes
            connection.commit();
            //Start D3S data fetching
            metadataManager.fetchProcessingDatasetsMetadata(surveyCode);
        } catch (Exception ex) {
            connection.rollback();
            throw ex;
        } finally {
            fileManager.removeTmpFolder(tmpFolder);
            connection.close();
        }
    }

    private Integer getSurveyYear (MeIdentification<DSDDataset> metadata) {
        MeContent meContent = metadata!=null ? metadata.getMeContent() : null;
        SeCoverage seCoverage = meContent!=null ? meContent.getSeCoverage() : null;
        OjPeriod ojPeriod = seCoverage!=null ? seCoverage.getCoverageTime() : null;
        Date from = ojPeriod!=null ? ojPeriod.getFrom() : null;
        if (from!=null) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(from);
            return calendar.get(Calendar.YEAR);
        } else
            return null;
    }
}