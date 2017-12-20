package org.fao.gift.rest.spi;

import org.fao.gift.common.dto.User;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

public interface UserSpi {

    @GET
    @Path("/{username}")
    @Produces(MediaType.APPLICATION_JSON)
    Response getUser(@PathParam("username") String username);

    @GET
    @Path("/{username}/jwt")
    @Produces(MediaType.APPLICATION_JSON)
    Response getJwt(@PathParam("username") String username);

    @GET
    @Path("/{username}/surveys")
    @Produces(MediaType.APPLICATION_JSON)
    Response getUserSurveys(@PathParam("username") String username);

//    @POST
//    @Consumes(MediaType.APPLICATION_JSON)
//    @Produces(MediaType.APPLICATION_JSON)
//    Response createUser(User user);

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    Response createAndOrGetUser(User user);
}