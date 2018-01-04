package org.fao.gift.notification.impl;

import org.fao.fenix.commons.msd.dto.full.MeIdentification;
import org.fao.gift.common.dto.MainConfig;
import org.fao.gift.common.dto.User;
import org.fao.gift.common.utils.D3SClient;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.core.Response;

@ApplicationScoped
public class NotificationLogic {

    private static final String D3S_BASEURL = "gift.d3s.url";
    @Inject D3SClient d3SClient;
    @Inject MainConfig config;


    public Response insertMetadata(MeIdentification metadata) throws Exception {
      return d3SClient.insertMetadata(config.get(D3S_BASEURL), metadata);
    }

    public Response updateMetadata(MeIdentification metadata) throws Exception {
        return d3SClient.updateMetadata(config.get(D3S_BASEURL), metadata);
    }

    public Response deleteMetadataByUID(String uid) throws Exception {
        return d3SClient.deleteMetadata(config.get(D3S_BASEURL), uid, null);
    }

    public Response deleteMetadata(String uid, String version) throws Exception {
        return d3SClient.deleteMetadata(config.get(D3S_BASEURL), uid, version);
    }

}
