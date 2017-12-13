package org.fao.gift.rest.spi;

import org.fao.fenix.commons.msd.dto.templates.identification.MeIdentification;
import org.fao.fenix.commons.utils.PATCH;
import org.fao.gift.common.dto.User;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

public interface NotificationSpi {

    /**
     * Inserting a new metadata
     */
    @POST
    @Path("/metadata")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public <T extends org.fao.fenix.commons.msd.dto.full.MeIdentification> MeIdentification insertMetadata(User user,T metadata) throws Exception;

    /**
     * Modifying an existing metadata
     */

    @PUT
    @Path("/metadata")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public <T extends org.fao.fenix.commons.msd.dto.full.MeIdentification> MeIdentification updateMetadata(User user,T metadata) throws Exception;


    /**
     * Patching an existing metadata
     */
    @PATCH
    @Path("/metadata")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public <T extends org.fao.fenix.commons.msd.dto.full.MeIdentification> MeIdentification appendMetadata(User user,T metadata) throws Exception;


    @DELETE
    @Path("/metadata/uid/{uid}")
    public String deleteMetadataByUID(@PathParam("uid") String uid) throws Exception;
    @DELETE
    @Path("/metadata/{uid}/{version}")
    public String deleteMetadata(@PathParam("uid") String uid, @PathParam("version") String version) throws Exception;
}
