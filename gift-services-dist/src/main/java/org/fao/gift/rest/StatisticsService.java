package org.fao.gift.rest;

import io.swagger.annotations.Api;
import org.fao.fenix.commons.find.dto.filter.StandardFilter;
import org.fao.fenix.commons.msd.dto.full.DSDDataset;
import org.fao.fenix.commons.msd.dto.full.MeIdentification;
import org.fao.gift.common.dto.Survey;
import org.fao.gift.common.dto.User;
import org.fao.gift.common.dto.UserRole;
import org.fao.gift.common.dto.UserWithFilter;
import org.fao.gift.commons.dto.search.StatisticsParameters;
import org.fao.gift.rest.spi.StatisticsSpi;
import org.fao.gift.services.FilteringLogic;
import org.fao.fenix.commons.msd.dto.templates.ResponseBeanFactory;
import org.fao.gift.services.UserLogic;

import javax.inject.Inject;
import javax.ws.rs.*;
import java.util.Collection;
import java.util.List;

@Api(value = "filter")
@Path("statistics")
public class StatisticsService implements StatisticsSpi {
    @Inject FilteringLogic filteringLogic;
    @Inject UserLogic userLogic;


    @Override
    public Collection<Object> filterForStatistics(StandardFilter fenixFilter, boolean full) throws Exception {
        Collection<MeIdentification<DSDDataset>> data = filteringLogic.filterForStatistics(new StatisticsParameters(fenixFilter));
        return ResponseBeanFactory.getInstances(getProxyClass(full), data);
    }


    private Class getProxyClass(boolean full) {
        if (full)
            return org.fao.fenix.commons.msd.dto.templates.standard.combined.dataset.Metadata.class;
        return org.fao.gift.commons.dto.template.statistics.MeIdentification.class;
    }


    public Collection<Object> filterForStatistics(UserWithFilter userWithFilter, boolean full) throws Exception {
        checkParamters(userWithFilter);
        // filter datasets based on the metadata parameter
        Collection<MeIdentification<DSDDataset>> d3sData = filteringLogic.filterForStatistics(new StatisticsParameters(userWithFilter.getFilter()));
        User user = userWithFilter.getUser();

        // if the user is not an admin, filter again on the user
        if(!user.getRole().equals(UserRole.ADMIN)) {
            List<Survey> userSurveys = userLogic.getUserSurveys(user.getUsername());
            return ResponseBeanFactory.getInstances(getProxyClass(full), filteringLogic.filterForStatistics(d3sData,userSurveys));
        }
        return ResponseBeanFactory.getInstances(getProxyClass(full), d3sData);
    }

    private void checkParamters (UserWithFilter userWithFilter) throws Exception{
        if(userWithFilter == null || userWithFilter.getUser() == null || userWithFilter.getFilter() == null)
            throw new Exception("Some paramters miss (User or Filter");
    }
}