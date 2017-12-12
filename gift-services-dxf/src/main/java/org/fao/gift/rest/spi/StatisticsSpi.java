package org.fao.gift.rest.spi;

import org.fao.fenix.commons.find.dto.filter.StandardFilter;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.Collection;

@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public interface StatisticsSpi {

    @POST
    @Path("/filter")
    Collection<Object> filterForStatistics(StandardFilter fenixFilter, @QueryParam("full") @DefaultValue("false") boolean full) throws Exception;

}