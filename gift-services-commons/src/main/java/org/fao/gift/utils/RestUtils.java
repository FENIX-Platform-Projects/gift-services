package org.fao.gift.utils;

import org.fao.gift.dto.Outcome;

import javax.ws.rs.core.Response;

public class RestUtils {

    public static Response getBaseResponse(Response.Status status) {
        return Response.status(status).entity(Outcome.create(status)).build();
    }
}
