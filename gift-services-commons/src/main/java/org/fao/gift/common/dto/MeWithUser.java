package org.fao.gift.common.dto;

import org.fao.fenix.commons.msd.dto.full.MeIdentification;

public class MeWithUser {

    private User user;
    private MeIdentification metadata;

    public MeWithUser() {
    }

    public MeWithUser(User user, MeIdentification metadata) {
        this.user = user;
        this.metadata = metadata;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public MeIdentification getMetadata() {
        return metadata;
    }

    public void setMetadata(MeIdentification metadata) {
        this.metadata = metadata;
    }
}
