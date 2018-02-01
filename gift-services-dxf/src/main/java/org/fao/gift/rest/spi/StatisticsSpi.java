package org.fao.gift.rest.spi;

import io.swagger.annotations.ApiOperation;
import org.fao.fenix.commons.find.dto.filter.StandardFilter;
import org.fao.gift.common.dto.UserWithFilter;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.Collection;

@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public interface StatisticsSpi {

    @POST
    @Path("/filter")
    @ApiOperation(value = "Filtering metadata based on parameters",
            notes = "Filter gift metadata",
            response = Collection.class,
            responseContainer = "Object")
    Collection<Object> filterForStatistics(StandardFilter fenixFilter, @QueryParam("full") @DefaultValue("false") boolean full) throws Exception;


    @POST
    @Path("/filter")
    @ApiOperation(value = "Filtering metadata based on parameters and user",
            notes = "Filter gift metadata on user ",
            response = Collection.class,
            responseContainer = "Object")
    Collection<Object> filterForStatistics(UserWithFilter userWithFilter, @QueryParam("full") @DefaultValue("false") boolean full) throws Exception;
}