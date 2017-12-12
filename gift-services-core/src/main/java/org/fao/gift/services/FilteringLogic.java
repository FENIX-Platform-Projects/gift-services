package org.fao.gift.services;

import org.fao.fenix.commons.msd.dto.full.DSDDataset;
import org.fao.fenix.commons.msd.dto.full.MeIdentification;
import org.fao.gift.dao.impl.FilteringDao;
import org.fao.gift.commons.dto.MainConfig;
import org.fao.gift.commons.dto.search.StatisticsParameters;
import org.fao.gift.commons.utils.D3SClient;

import javax.inject.Inject;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

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


}
