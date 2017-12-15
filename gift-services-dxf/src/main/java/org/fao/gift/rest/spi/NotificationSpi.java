package org.fao.gift.rest.spi;

import org.fao.fenix.commons.utils.PATCH;
import org.fao.gift.common.dto.MeWithUser;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

public interface NotificationSpi {

    /**
     * Inserting a new metadata
     */
    @POST
    @Path("/metadata")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    <T extends org.fao.fenix.commons.msd.dto.full.MeIdentification> Response insertMetadata(MeWithUser meForum) throws Exception;

    /**
     * Modifying an existing metadata
     */

    @PUT
    @Path("/metadata")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    <T extends org.fao.fenix.commons.msd.dto.full.MeIdentification> Response updateMetadata(MeWithUser meForum) throws Exception;


    /**
     * Patching an existing metadata
     */
    @PATCH
    @Path("/metadata")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    <T extends org.fao.fenix.commons.msd.dto.full.MeIdentification> Response appendMetadata(MeWithUser meForum) throws Exception;


    @DELETE
    @Path("/metadata/uid/{uid}")
    Response deleteMetadataByUID(@PathParam("uid") String uid) throws Exception;

    @DELETE
    @Path("/metadata/{uid}/{version}")
    Response deleteMetadata(@PathParam("uid") String uid, @PathParam("version") String version) throws Exception;
}
