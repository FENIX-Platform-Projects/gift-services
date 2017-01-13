package org.fao.gift.dto.template.statistics;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.fao.fenix.commons.msd.dto.templates.ResponseHandler;
import org.fao.fenix.commons.msd.dto.templates.standard.metadata.OjCodeList;
import org.fao.fenix.commons.msd.dto.templates.standard.metadata.OjPeriod;

public class SeCoverage extends ResponseHandler {

    public SeCoverage() {}
    public SeCoverage(Object ... source) {
        super(source);
    }



    @JsonProperty
    public OjCodeList getCoverageGeographic() {
        return null;
    }
    @JsonProperty
    public OjPeriod getCoverageTime() {
        return null;
    }

}
