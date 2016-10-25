package org.fao.gift.upload;

import org.fao.ess.uploader.core.dto.ChunkMetadata;
import org.fao.ess.uploader.core.dto.FileMetadata;
import org.fao.ess.uploader.core.init.UploaderConfig;
import org.fao.ess.uploader.core.metadata.MetadataStorage;
import org.fao.ess.uploader.core.process.PostUpload;
import org.fao.ess.uploader.core.process.ProcessInfo;
import org.fao.ess.uploader.core.storage.BinaryStorage;
import org.fao.gift.upload.impl.DataManager;
import org.fao.gift.upload.impl.FileManager;
import org.fao.gift.upload.impl.FoodGroups;
import org.fao.gift.upload.impl.MetadataManager;

import javax.inject.Inject;
import javax.ws.rs.BadRequestException;
import java.io.File;
import java.io.InputStream;
import java.util.Map;

@ProcessInfo(context = "gift.shared", name = "GiftSharedFolder", priority = 1)
public class GiftShared implements PostUpload {
    @Inject private FileManager fileManager;
    @Inject private DataManager dataManager;
    @Inject private FoodGroups foodGroups;
    @Inject private MetadataManager metadataManager;
    @Inject private UploaderConfig config;



    @Override
    public void chunkUploaded(ChunkMetadata metadata, MetadataStorage metadataStorage, BinaryStorage storage) throws Exception {
        //Nothing to do here
    }

    @Override
    public void fileUploaded(FileMetadata metadata, MetadataStorage metadataStorage, BinaryStorage storage, Map<String, Object> processingParams) throws Exception {
        String survey = (String)processingParams.get("source");
        if (survey==null)
            throw new BadRequestException("Source is undefined");
        mainLogic(survey, metadata.getName(), storage.readFile(metadata, null));
    }

    public void mainLogic(String surveyCode, String fileName, InputStream zipFileInput) throws Exception {
        //Create temporary folder with zip file content
        File tmpFolder = fileManager.createTmpFolder();
        try {
            File file = fileManager.saveFile(tmpFolder, fileName, zipFileInput);
            //Transfer source file for bulk download
            fileManager.publishMetadataAttachmentFile(file, metadataManager.getMetadataUid(surveyCode));
            //Override existing metadata link information
            metadataManager.updateMetadataAttachments(surveyCode, fileName);
        } catch (Exception ex) {
            throw ex;
        } finally {
            fileManager.removeTmpFolder(tmpFolder);
        }
    }


}