package org.fao.gift.notification.impl;

import org.fao.fenix.commons.msd.dto.full.MeIdentification;
import org.fao.gift.common.dto.User;
import org.fao.gift.common.utils.D3SClient;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.core.Response;

@ApplicationScoped
public class NotificationLogic {

    private static final String D3S_BASEURL = "gift.d3s.url";

    @Inject D3SClient d3SClient;


    public <T extends MeIdentification> org.fao.fenix.commons.msd.dto.templates.identification.MeIdentification insertMetadata(org.fao.gift.common.dto.User user, T metadata) throws Exception {

        //Response response = d3SClient.insertMetadata(config.get(D3S_BASEURL), metadata);

        System.out.println("stop");

        return null;
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
