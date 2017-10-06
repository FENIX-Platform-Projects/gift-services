package org.fao.gift.rest.spi;

import org.fao.gift.dto.Disclaimer;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

public interface DisclaimerSpi {

    @GET
    String getDisclaimer(@QueryParam("uid") String surveyCode, @QueryParam("lang") String lang) throws Exception;

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    String updateDisclaimer(Disclaimer disclaimer) throws Exception;

}