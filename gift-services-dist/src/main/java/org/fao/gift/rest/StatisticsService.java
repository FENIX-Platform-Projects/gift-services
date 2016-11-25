package org.fao.gift.rest;

import org.fao.fenix.commons.find.dto.filter.StandardFilter;
import org.fao.gift.dto.search.StatisticsParameters;
import org.fao.gift.dto.template.statistics.MeIdentification;
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
    public Collection<MeIdentification> filterForStatistics(StandardFilter fenixFilter) throws Exception {
        return ResponseBeanFactory.getInstances(MeIdentification.class, filteringLogic.filterForStatistics(new StatisticsParameters(fenixFilter)));
    }

}