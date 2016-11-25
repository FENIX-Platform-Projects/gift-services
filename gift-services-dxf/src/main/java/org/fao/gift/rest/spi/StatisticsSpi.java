package org.fao.gift.rest.spi;

import org.fao.fenix.commons.find.dto.filter.StandardFilter;
import org.fao.gift.dto.template.statistics.MeIdentification;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.Collection;

@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public interface StatisticsSpi {

    @POST
    @Path("/filter")
    Collection<MeIdentification> filterForStatistics(StandardFilter fenixFilter) throws Exception;

}