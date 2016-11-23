package org.fao.gift.rest;

import org.fao.gift.upload.GiftBulk;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("test")
public class TestService {
    @Inject GiftBulk giftBulkManager;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getProcessStatus() {
        try {
            giftBulkManager.mainLogic("000042BUR201001", this.getClass().getResourceAsStream("/test/burkina_test1.zip"));
            return "OK";
        } catch (Exception e) {
            e.printStackTrace();
            return "ERROR: "+e.getMessage();
        }
    }
}
