package org.fao.gift.dto.template.statistics;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.fao.fenix.commons.msd.dto.templates.ResponseHandler;
import org.fao.fenix.commons.msd.dto.templates.identification.DSD;

import java.util.Collection;
import java.util.Map;

public class MeIdentification extends ResponseHandler {
    private Collection<org.fao.fenix.commons.msd.dto.templates.identification.MeIdentification> children;

    public MeIdentification() {}
    public MeIdentification(Object ... source) {
        super(source);
    }


    @JsonProperty
    public String getUid() {
        return null;
    }
    @JsonProperty
    public String getVersion() {
        return null;
    }
    @JsonProperty
    public Map<String, String> getTitle() {
        return null;
    }
    @JsonProperty
    public DSD getDsd() {
        return null;
    }

    @JsonProperty
    public Long getSampleSize() {
        Map<String,Object> additions = getAdditions();
        Map<String,Object> populationInformation = additions!=null ? (Map<String,Object>)additions.get("SampledPopulationInformation") : null;
        return populationInformation!=null ? (Long) populationInformation.get("") : null;
    }


    public Map<String,Object> getAdditions() {
        return null;
    }

}
