package org.fao.gift.upload.impl;

import org.fao.ess.uploader.core.init.UploaderConfig;
import org.fao.fenix.commons.msd.dto.full.*;
import org.fao.fenix.commons.msd.dto.type.DocumentType;
import org.fao.gift.upload.dto.Items;
import org.fao.gift.upload.dto.MetadataTemplates;
import org.fao.gift.utils.D3SClient;
import org.fao.fenix.commons.utils.FileUtils;
import org.fao.fenix.commons.utils.Groups;
import org.fao.fenix.commons.utils.JSONUtils;

import javax.inject.Inject;
import java.io.InputStream;
import java.util.*;

public class MetadataManager {

    @Inject private UploaderConfig config;
    @Inject private FileUtils fileUtils;
    @Inject private D3SClient d3SClient;


    public String createMetadataAttachmentLink(String surveyCode, String fileName) {
        return config.get("gift.remote.url.prefix.metadata.attachment")+'/'+surveyCode+'/'+fileName;
    }

    public void updateMetadataAttachments(String surveyCode, String fileName) throws Exception {
        String d3sBaseURL = config.get("gift.d3s.url");
        d3sBaseURL = d3sBaseURL + (d3sBaseURL.charAt(d3sBaseURL.length() - 1) != '/' ? "/" : "");

        //Retrieve existing meDocument by link or create a new one
        String link = createMetadataAttachmentLink(surveyCode, fileName);
        MeIdentification<DSDDataset> existingMetadata = loadSurveyMetadata(surveyCode);
        Collection<MeDocuments> documents = existingMetadata.getMeDocuments();
        if (documents==null)
            documents = new LinkedList<>();
        MeDocuments meDocuments = null;
        for (MeDocuments document : documents)
            if (document.getDocument()!=null && link.equalsIgnoreCase(document.getDocument().getLink()))
                meDocuments = document;
        if (meDocuments==null) {
            documents.add(meDocuments = new MeDocuments());
            OjCitation document = new OjCitation();
            meDocuments.setDocument(document);
            document.setDocumentKind(DocumentType.other);
            Map<String,String> title = new HashMap<>();
            title.put("EN", fileName);
            document.setTitle(title);
        }
        //Update date
        OjCitation document = meDocuments.getDocument();
        document.setDate(new Date());
        //Save metadata links information
        MeIdentification<DSDDataset> metadata = new MeIdentification<>();
        metadata.setUid(existingMetadata.getUid());
        metadata.setVersion(existingMetadata.getVersion());
        metadata.setMeDocuments(documents);
        d3SClient.updateMetadata(d3sBaseURL, metadata, false);
    }

    public MeIdentification<DSDDataset> loadSurveyMetadata(String surveyCode) throws Exception {
        String d3sBaseURL = config.get("gift.d3s.url");
        d3sBaseURL = d3sBaseURL + (d3sBaseURL.charAt(d3sBaseURL.length() - 1) != '/' ? "/" : "");

        //load metadata
        return d3SClient.getDatasetMetadata(d3sBaseURL, surveyCode, null);
    }

    public void updateSurveyMetadata(String surveyCode) throws Exception {
        String d3sBaseURL = config.get("gift.d3s.url");
        d3sBaseURL = d3sBaseURL + (d3sBaseURL.charAt(d3sBaseURL.length() - 1) != '/' ? "/" : "");

        //Create metadata bean
        MeIdentification<DSDDataset> metadata = new MeIdentification<>();
        metadata.setUid(surveyCode);
        MeMaintenance meMaintenance = new MeMaintenance();
        metadata.setMeMaintenance(meMaintenance);
        SeUpdate seUpdate = new SeUpdate();
        seUpdate.setUpdateDate(new Date());
        meMaintenance.setSeUpdate(seUpdate);

        d3SClient.appendDatasetMetadata(d3sBaseURL, metadata);
    }

    public void updateProcessingDatasetsMetadata (String survey) throws Exception {
        String d3sBaseURL = config.get("gift.d3s.url");
        d3sBaseURL = d3sBaseURL + (d3sBaseURL.charAt(d3sBaseURL.length() - 1) != '/' ? "/" : "");

        Collection<MeIdentification<DSDDataset>> newMetadatyaList = createMetadata(survey);
        Collection<MeIdentification<DSDDataset>> existingMetadatyaList = loadExistingProcessMetadata(d3sBaseURL);
        Groups<MeIdentification<DSDDataset>> metadataGroups = new Groups(newMetadatyaList, existingMetadatyaList);

        d3SClient.deleteMetadata(d3sBaseURL, metadataGroups.update);
        d3SClient.insertMetadata(d3sBaseURL, newMetadatyaList);
    }

    private Collection<MeIdentification<DSDDataset>> createMetadata (String survey) throws Exception {
        Collection<MeIdentification<DSDDataset>> metadataInstances = new LinkedList<>();
        for (MetadataTemplates template : MetadataTemplates.values()) {
            MeIdentification<DSDDataset> metadata = loadMetadataTemplate(template);
            String uid = metadata.getUid();
            if (template.bySurvey)
                uid += '_' + survey;
            if (template.byItem)
                for (Items item : Items.values()) {
                    metadata.setUid(uid + '_' + item);
                    metadataInstances.add(metadata);
                }
            else {
                metadata.setUid(uid);
                metadataInstances.add(metadata);
            }
        }
        return metadataInstances;
    }

    private Collection<MeIdentification<DSDDataset>> loadExistingProcessMetadata(String d3sBaseURL) throws Exception {
        return d3SClient.retrieveMetadata(d3sBaseURL, "gift_process");
    }

    //Utils
    private static final String templatePath = "/gift/metadata/templates/";
    private MeIdentification<DSDDataset> loadMetadataTemplate(MetadataTemplates template) throws Exception {
        InputStream templateStream = this.getClass().getResourceAsStream(templatePath+template.toString()+".json");
        if (templateStream==null)
            return null;
        String templateContent = fileUtils.readTextFile(templateStream);
        return JSONUtils.decode(templateContent, MeIdentification.class, DSDDataset.class);
    }
}
