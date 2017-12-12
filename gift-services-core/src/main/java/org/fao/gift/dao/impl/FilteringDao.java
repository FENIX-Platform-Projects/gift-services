package org.fao.gift.dao.impl;

import org.fao.gift.dao.Dao;
import org.fao.fenix.commons.utils.database.DatabaseUtils;
import org.fao.gift.commons.dto.search.StatisticsParameters;

import javax.inject.Inject;
import java.sql.*;
import java.util.*;

public class FilteringDao extends Dao {

    @Inject
    DatabaseUtils databaseUtils;


    public Set<String> getSurveyCodes(StatisticsParameters parameters) throws Exception {
        Set<String> surveyCodes = new HashSet<>();
        Connection connection = getConnection();
        try {
            Collection<Object> queryParameters = new LinkedList<>();
            for (ResultSet resultSet = databaseUtils.fillStatement(
                    connection.prepareStatement("SELECT survey_code FROM survey_index_data" + buildSurveysWhere(parameters, queryParameters)),
                    null,
                    queryParameters.toArray()
            ).executeQuery(); resultSet.next(); )
                surveyCodes.add(resultSet.getString(1));
        } finally {
            connection.close();
        }
        return surveyCodes;
    }


    private String buildSurveysWhere(StatisticsParameters params, Collection<Object> queryParams) {
        StringBuilder where = new StringBuilder();

        if (params.special_condition != null && params.special_condition.size() > 0) {
            where.append(" AND ");
            if (params.gender == null) {
                where.append("(gender &&  ARRAY[?::text,?::text] OR ");
                queryParams.addAll(Arrays.asList("1", "3"));
            }

            where.append("special_condition &&  ARRAY[");
            for (String specialCondition : params.special_condition)
                if (specialCondition.equals("1")) { //Pregnant
                    where.append("?::text,?::text,");
                    queryParams.addAll(Arrays.asList("2", "8"));
                } else if (specialCondition.equals("2")) { //Lactating
                    where.append("?::text,?::text,");
                    queryParams.addAll(Arrays.asList("3", "9"));
                } else if (specialCondition.equals("3")) { //Pregnant and lactating
                    where.append("?::text,?::text,");
                    queryParams.addAll(Arrays.asList("4", "11"));
                } else if (specialCondition.equals("4")) { //Non pregnant and non lactating
                    where.append("?::text,?::text,?::text,?::text,?::text,");
                    queryParams.addAll(Arrays.asList("1", "5", "6", "7", "10"));
                }
            where.setCharAt(where.length() - 1, ']');

            if (params.gender == null)
                where.append(')');
        } else if (params.gender != null) {
            where.append(" AND gender &&  ARRAY[?::text]");
            queryParams.add(params.gender);
        }

        if (params.age_year != null && (params.age_year.from != null || params.age_year.to != null)) {
            where.append(" AND age_year && NUMRANGE(?::numeric,?::numeric,'[]')");
            queryParams.add(params.age_year.from != null ? params.age_year.from : 0.0);
            queryParams.add(params.age_year.to != null ? params.age_year.to : 9999.0);
        }
        if (params.age_month != null && (params.age_month.from != null || params.age_month.to != null)) {
            where.append(" AND age_month && NUMRANGE(?::numeric,?::numeric,'[]')");
            queryParams.add(params.age_month.from != null ? params.age_month.from : 0.0);
            queryParams.add(params.age_month.to != null ? params.age_month.to : 9999.0 * 12);
        }

        if (params.foodex2_code != null && params.foodex2_code.size() > 0) {
            where.append(" AND foodex2_code &&  ARRAY[");
            for (int i = 0, l = params.foodex2_code.size(); i < l; i++)
                where.append("?::text,");
            where.setCharAt(where.length() - 1, ']');
            queryParams.addAll(params.foodex2_code);
        }

        return where.length() > 0 ? " WHERE" + where.substring(4) : "";
    }


}