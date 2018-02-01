package org.fao.gift.services;

import org.fao.fenix.commons.msd.dto.full.DSDDataset;
import org.fao.fenix.commons.msd.dto.full.MeIdentification;
import org.fao.gift.common.dto.Survey;
import org.fao.gift.dao.impl.FilteringDao;
import org.fao.gift.common.dto.MainConfig;
import org.fao.gift.commons.dto.search.StatisticsParameters;
import org.fao.gift.common.utils.D3SClient;

import javax.inject.Inject;
import java.util.*;

public class FilteringLogic {
    @Inject
    MainConfig config;
    @Inject
    FilteringDao filteringDao;
    @Inject
    D3SClient d3SClient;


    public Collection<MeIdentification<DSDDataset>> filterForStatistics(StatisticsParameters parameters) throws Exception {
        //Set parameters
        String d3sBaseURL = config.get("gift.d3s.url");
        d3sBaseURL = d3sBaseURL + (d3sBaseURL.charAt(d3sBaseURL.length() - 1) != '/' ? "/" : "");
        //Filter by metadata (country and survey selectors)
        Collection<MeIdentification<DSDDataset>> metadataList = d3SClient.retrieveMetadataForStatistics(
                d3sBaseURL,
                "gift",
                parameters.countries,
                parameters.referenceArea,
                parameters.coverageSector,
                parameters.year,
                parameters.confidentialityStatus
        );
        //Filter again by data (food and population selectors)
        Set<String> surveysCode = filteringDao.getSurveyCodes(parameters);
        for (Iterator<MeIdentification<DSDDataset>> metadataIterator = metadataList.iterator(); metadataIterator.hasNext(); ) {
            MeIdentification<DSDDataset> metadata = metadataIterator.next();
            metadata.getAdditions();
            if (!surveysCode.contains(metadata.getUid()))
                metadataIterator.remove();
        }
        //return retrieved list
        return metadataList;
    }


    public Collection<MeIdentification<DSDDataset>> filterForStatistics(Collection<MeIdentification<DSDDataset>> d3sDatasets, List<Survey> userSurveys) throws Exception {
        Collection<MeIdentification<DSDDataset>> metadataBelongingToUser = new LinkedList<>();
        // Get all the user's survey ids
        Set<String> surveysIds = getSurveyUidsFromList(userSurveys);
        // create a new collection of metadata based on the metadata created by the user
        for(MeIdentification<DSDDataset> metadata: d3sDatasets)
            if(metadata.getUid()!= null && surveysIds.contains(metadata.getUid()))
                metadataBelongingToUser.add(metadata);
        return metadataBelongingToUser;
    }


    // Utils
    private Set<String> getSurveyUidsFromList (List<Survey> surveys) {
        Set<String> result = new HashSet<>();
        for(Survey survey: surveys)
            result.add(survey.getSurveyId());
        return result;
    }


}
