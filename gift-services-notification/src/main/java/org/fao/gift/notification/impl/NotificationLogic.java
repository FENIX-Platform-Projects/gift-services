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


    public Response insertMetadata(org.fao.gift.common.dto.User user,MeIdentification metadata) throws Exception {

      return d3SClient.insertMetadata(config.get(D3S_BASEURL), metadata);

    }

    public <T extends MeIdentification> org.fao.fenix.commons.msd.dto.templates.identification.MeIdentification updateMetadata(T metadata) throws Exception {
        return null;
    }

    public String deleteMetadataByUID(String uid) throws Exception {
        return null;
    }

    public String deleteMetadata(String uid, String version) throws Exception {
        return null;
    }

}
