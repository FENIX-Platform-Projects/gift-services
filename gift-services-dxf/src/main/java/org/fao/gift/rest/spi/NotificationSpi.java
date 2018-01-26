package org.fao.gift.rest.spi;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;
import org.fao.fenix.commons.utils.PATCH;
import org.fao.gift.common.dto.MeWithUser;
import org.fao.gift.commons.dto.template.statistics.MeIdentification;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
@Api(value = "metadata")

public interface NotificationSpi {

    /**
     * Inserting a new metadata
     */
    @POST
    @Path("/metadata")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "User insert metadata",
            notes = "A single user want to insert a metadata",
            response = MeIdentification.class,
            responseContainer = "Object")
    <T extends org.fao.fenix.commons.msd.dto.full.MeIdentification> Response insertMetadata(MeWithUser meForum) throws Exception;

    /**
     * Modifying an existing metadata
     */
    @PUT
    @Path("/metadata")
    @ApiOperation(value = "User update metadata",
            notes = "A single user want to update a metadata",
            response = MeIdentification.class,
            responseContainer = "Object")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    <T extends org.fao.fenix.commons.msd.dto.full.MeIdentification> Response updateMetadata(MeWithUser meForum) throws Exception;


    /**
     * Patching an existing metadata
     */
    @PATCH
    @Path("/metadata")
    @ApiOperation(value = "User append metadata",
            notes = "A single user want to append fields to an existing metadata",
            response = MeIdentification.class,
            responseContainer = "Object")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    <T extends org.fao.fenix.commons.msd.dto.full.MeIdentification> Response appendMetadata(MeWithUser meForum) throws Exception;


    @DELETE
    @Path("/metadata/uid/{uid}")
    @ApiOperation(value = "Remove a resource by uid",hidden = true)
    Response deleteMetadataByUID(@PathParam("uid") String uid) throws Exception;

    @DELETE
    @Path("/metadata/{uid}/{version}")
    @ApiOperation(value = "Remove a resource by uid and version",hidden = true)
    Response deleteMetadata(@PathParam("uid") String uid, @PathParam("version") String version) throws Exception;
}
