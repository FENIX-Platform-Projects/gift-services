package org.fao.gift.rest;

import org.fao.fenix.commons.find.dto.filter.StandardFilter;
import org.fao.fenix.commons.msd.dto.full.DSDDataset;
import org.fao.fenix.commons.msd.dto.full.MeIdentification;
import org.fao.gift.dto.search.StatisticsParameters;
import org.fao.gift.rest.spi.StatisticsSpi;
import org.fao.gift.services.FilteringLogic;
import org.fao.fenix.commons.msd.dto.templates.ResponseBeanFactory;

import javax.inject.Inject;
import javax.ws.rs.*;
import java.util.Collection;

@Path("statistics")
public class StatisticsService implements StatisticsSpi {
    @Inject FilteringLogic filteringLogic;


    @Override
    public Collection<Object> filterForStatistics(StandardFilter fenixFilter, boolean full) throws Exception {
        Collection<MeIdentification<DSDDataset>> data = filteringLogic.filterForStatistics(new StatisticsParameters(fenixFilter));
        return ResponseBeanFactory.getInstances(getProxyClass(full), data);
    }


    private Class getProxyClass(boolean full) {
        if (full)
            return org.fao.fenix.commons.msd.dto.templates.standard.combined.dataset.Metadata.class;
        return org.fao.gift.dto.template.statistics.MeIdentification.class;
    }

}