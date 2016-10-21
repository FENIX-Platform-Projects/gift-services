package org.fao.gift.services;

import org.fao.gift.dao.impl.SelectorsDao;
import org.fao.fenix.commons.msd.dto.full.Code;
import org.fao.fenix.commons.msd.dto.full.Period;
import org.fao.fenix.commons.msd.dto.type.DataType;
import org.fao.gift.dto.MainConfig;
import org.fao.gift.utils.D3SClient;
import org.fao.gift.utils.FenixUtils;

import javax.inject.Inject;
import java.util.Collection;
import java.util.Date;

public class SelectorsLogic {
    @Inject MainConfig config;
    @Inject FenixUtils fenixUtils;
    @Inject SelectorsDao selectorsDao;
    @Inject D3SClient d3SClient;

    public Period getPolicyTimePeriod(DataType type) throws Exception {
        Date[] dates = selectorsDao.getPolicyPeriod();
        return fenixUtils.toPeriod(dates[0], dates[1], type);
    }

    public Collection<Code> getPolicyTypes(String commodityDomain) throws Exception {
        return tomcatNormalization(d3SClient.filterCodelist(getD3sBaseUrl(), "OECD_PolicyType", "1.0", selectorsDao.getPolicyTypes(commodityDomain)));
    }
    public Collection<Code> getPolicyMeasures(String commodityDomain, String policyDomain, String policyType) throws Exception {
        return tomcatNormalization(d3SClient.filterCodelist(getD3sBaseUrl(), "OECD_PolicyMeasure", "1.0", selectorsDao.getPolicyMeasures(commodityDomain, policyDomain, policyType)));
    }
    public Collection<Code> getCountries(String policyMeasure, String policyType) throws Exception {
        return tomcatNormalization(d3SClient.filterCodelist(getD3sBaseUrl(), "OECD_Country", "1.0", selectorsDao.getCountries(policyMeasure, policyType)));
    }
    public Collection<Code> getCommodityClasses(String commodityDomain) throws Exception {
        return tomcatNormalization(d3SClient.filterCodelist(getD3sBaseUrl(), "OECD_CommodityClass", "1.0", selectorsDao.getCommodityClasses(commodityDomain)));
    }


    //Utils
    private String getD3sBaseUrl() {
        String baseUrl = config.get("policy.d3s.url");
        return baseUrl + (baseUrl.charAt(baseUrl.length() - 1) != '/' ? "/" : "");
    }
    //TODO remove it with jackson configuration
    private Collection<Code> tomcatNormalization(Collection<Code> codes) {
        if (codes!=null)
            for (Code code : codes)
                code.setORID(null);
        return codes;
    }



}
