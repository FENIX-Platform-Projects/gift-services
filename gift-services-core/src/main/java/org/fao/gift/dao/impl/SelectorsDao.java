package org.fao.gift.dao.impl;

import org.fao.gift.dao.Dao;
import org.fao.fenix.commons.utils.database.DatabaseUtils;

import javax.inject.Inject;
import java.sql.*;
import java.util.*;
import java.util.Date;

public class SelectorsDao extends Dao {

    @Inject DatabaseUtils databaseUtils;

    public Date[] getPolicyPeriod() throws Exception {
        Connection connection = getConnection();
        try {
            Date[] dates = new Date[2];
            ResultSet result = connection.createStatement().executeQuery("SELECT NULLIF(MIN(COALESCE(start_date,'0001-01-01')),'0001-01-01') AS start_date, NULLIF(MAX(COALESCE(end_date,'9999-12-31')),'9999-12-31') AS end_date FROM policy");
            if (result.next()) {
                dates[0] = result.getDate(1);
                dates[1] = result.getDate(2);
            }
            return dates;
        } finally {
            connection.close();
        }
    }

    public Collection<String> getPolicyTypes(String commodityDomain) throws Exception {
        return standardCodelistFilter(
                "SELECT DISTINCT (policytype_code) FROM cpl",
                new String[]{"commoditydomain_code"},
                new Object[]{commodityDomain}
        );
    }

    public Collection<String> getPolicyMeasures(String commodityDomain, String policyDomain, String policyType) throws Exception {
        return standardCodelistFilter(
                "SELECT DISTINCT (policymeasure_code) FROM cpl",
                new String[]{"commoditydomain_code", "policydomain_code", "policytype_code"},
                new Object[]{commodityDomain, policyDomain, policyType}
        );
    }

    public Collection<String> getCountries(String policyMeasure, String policyType) throws Exception {
        return standardCodelistFilter(
                "SELECT DISTINCT (country_code) FROM cpl",
                new String[]{"policymeasure_code", "policytype_code"},
                new Object[]{policyMeasure, policyType}
        );
    }

    public Collection<String> getCommodityClasses(String commodityDomain) throws Exception {
        return standardCodelistFilter(
                "SELECT DISTINCT (commodityclass_code) FROM cpl",
                new String[]{"commoditydomain_code"},
                new Object[]{commodityDomain}
        );
    }


    //Utils
    public Collection<String> standardCodelistFilter (String baseQuery, String[] names, Object[] values) throws Exception {
        Connection connection = getConnection();
        try {
            Collection<Object> params = new LinkedList<>();
            PreparedStatement statement = connection.prepareStatement(baseQuery+buildStandardFilter(
                    names,
                    values,
                    params
            ));
            databaseUtils.fillStatement(statement, null, params.toArray());
            ResultSet result = statement.executeQuery();

            Collection<String> codes = new LinkedList<>();
            while (result.next())
                codes.add(result.getString(1));
            return codes;
        } finally {
            connection.close();
        }
    }

}