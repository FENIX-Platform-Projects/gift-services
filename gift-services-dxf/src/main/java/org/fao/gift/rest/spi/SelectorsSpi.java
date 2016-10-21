package org.fao.gift.rest.spi;

import org.fao.fenix.commons.msd.dto.templates.codeList.Code;
import org.fao.fenix.commons.msd.dto.templates.export.metadata.Period;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.Collection;

@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public interface SelectorsSpi {


    @GET
    @Path("/period")
    Period getPolicyPeriod() throws Exception;

    @GET
    @Path("/policyTypes")
    Collection<Code> getPolicyTypes(@QueryParam("commodityDomain") String commodityDomain) throws Exception;

    @GET
    @Path("/policyMeasures")
    Collection<Code> getPolicyMeasures(@QueryParam("commodityDomain") String commodityDomain, @QueryParam("policyDomain") String policyDomain, @QueryParam("policyType") String policyType) throws Exception;

    @GET
    @Path("/countries")
    Collection<Code> getCountries(@QueryParam("policyMeasure") String policyMeasure, @QueryParam("policyType") String policyType) throws Exception;

    @GET
    @Path("/commodityClasses")
    Collection<Code> getCommodityClasses(@QueryParam("commodityDomain") String commodityDomain) throws Exception;

}