package org.fao.gift.commons.dto.template.statistics;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.fao.fenix.commons.msd.dto.templates.ResponseHandler;


public class MeContent extends ResponseHandler {

    public MeContent() {}
    public MeContent(Object ... source) {
        super(source);
    }


    @JsonProperty
    public SeCoverage getSeCoverage() {
        return null;
    }
}
