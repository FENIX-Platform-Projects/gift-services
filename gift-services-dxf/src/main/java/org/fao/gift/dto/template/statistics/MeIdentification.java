package org.fao.gift.dto.template.statistics;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.orientechnologies.orient.core.db.record.OIdentifiable;
import com.orientechnologies.orient.core.exception.OSerializationException;
import com.orientechnologies.orient.core.id.ORID;
import com.orientechnologies.orient.core.record.ORecord;
import com.orientechnologies.orient.core.serialization.OSerializableStream;
import org.fao.fenix.commons.msd.dto.templates.ResponseHandler;
import org.fao.fenix.commons.msd.dto.templates.identification.DSD;
import org.fao.fenix.commons.msd.dto.templates.standard.metadata.OjPeriod;

import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.*;

public class MeIdentification extends ResponseHandler {
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy");

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
    public MeContent getMeContent() {
        return null;
    }
    @JsonProperty
    public DSD getDsd() {
        return null;
    }

    @JsonProperty
    public Long getSampleSize() {
        Map<String,Object> additions = getAdditions();
        Map<String,Object> populationInformation = additions!=null ? (Map<String,Object>)additions.get("sampledPopulationInformation") : null;
        Number populationSize = populationInformation!=null ? (Number) populationInformation.get("sampleSize") : null;
        return populationSize!=null ? populationSize.longValue() : null;
    }
    @JsonProperty
    public Integer getYear() {
        MeContent meContent = getMeContent();
        SeCoverage seCoverage = meContent!=null ? meContent.getSeCoverage() : null;
        OjPeriod ojPeriod = seCoverage!=null ? seCoverage.getCoverageTime() : null;
        Date date = ojPeriod!=null ? ojPeriod.getFrom() : null;
        return date!=null ? new Integer(dateFormat.format(date)) : null;
    }

    public Map<String,Object> getAdditions() {
        return null;
    }



    //Utils

    @Override
    public ORID getORID() {
        return new ORID() {
            @Override
            public int getClusterId() {
                return 0;
            }

            @Override
            public long getClusterPosition() {
                return 0;
            }

            @Override
            public void reset() {

            }

            @Override
            public boolean isPersistent() {
                return false;
            }

            @Override
            public boolean isValid() {
                return false;
            }

            @Override
            public boolean isNew() {
                return false;
            }

            @Override
            public boolean isTemporary() {
                return false;
            }

            @Override
            public ORID copy() {
                return null;
            }

            @Override
            public String next() {
                return null;
            }

            @Override
            public ORID nextRid() {
                return null;
            }

            @Override
            public int toStream(OutputStream outputStream) throws IOException {
                return 0;
            }

            @Override
            public StringBuilder toString(StringBuilder stringBuilder) {
                return null;
            }

            @Override
            public ORID getIdentity() {
                return null;
            }

            @Override
            public <T extends ORecord> T getRecord() {
                return null;
            }

            @Override
            public void lock(boolean b) {

            }

            @Override
            public boolean isLocked() {
                return false;
            }

            @Override
            public void unlock() {

            }

            @Override
            public byte[] toStream() throws OSerializationException {
                return new byte[0];
            }

            @Override
            public OSerializableStream fromStream(byte[] bytes) throws OSerializationException {
                return null;
            }

            @Override
            public int compareTo(OIdentifiable o) {
                return 0;
            }

            @Override
            public int compare(OIdentifiable o1, OIdentifiable o2) {
                return 0;
            }
        };
    }

}
