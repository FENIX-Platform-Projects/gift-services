package org.fao.gift.dao.impl;

import org.fao.gift.dao.Dao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DisclaimerDao extends Dao {

    private static final Logger log = LoggerFactory.getLogger(DisclaimerDao.class);
    private final static String EMPTY_STRING = "";

    /**
     * Insert or update a Disclaimer.
     *
     * @param surveyCode the unique code of the Survey the Disclaimer belongs to
     * @param text       the content of the Disclaimer
     * @param lang       the Disclaimer language in two-letter ISO 639-1 (default: en)
     */
    public void updateBySurvey(final String surveyCode, final String text, String lang) throws SQLException {

        try (Connection connection = getConnection()) {
            /* From Postgres 9.5 the following query can be executed to handle the merge instead of the find + update
            "INSERT INTO disclaimer VALUES (?, ?, ?) ON CONFLICT (survey_code, lang) DO UPDATE SET text = EXCLUDED.text " */
            String existingDisclaimer = findBySurvey(surveyCode, lang);
            String upsertQuery = existingDisclaimer == null || existingDisclaimer.isEmpty() ?
                    "INSERT INTO disclaimer (text, survey_code, lang) VALUES (?, ?, ?) " :
                    "UPDATE disclaimer SET text = ? WHERE survey_code = ? AND lang = ? ";

            PreparedStatement insertOrUpdate = connection.prepareStatement(upsertQuery);

            insertOrUpdate.setBytes(1, text.getBytes());
            insertOrUpdate.setString(2, surveyCode);
            insertOrUpdate.setString(3, lang);

            int affectedRows = insertOrUpdate.executeUpdate();
            log.info("updateBySurvey - Affected {} rows", affectedRows);

            connection.commit();
        }
    }

    /**
     * Retrieve a Disclaimer text given the code of the Survey it's assigned to.
     *
     * @param surveyCode the unique code of the Survey
     * @param lang       the Disclaimer language in two-letter ISO 639-1 (default: en)
     * @return the Disclaimer content
     * @throws SQLException in case of errors accessing the DB
     */
    public String findBySurvey(final String surveyCode, String lang) throws SQLException {

        try (Connection connection = getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT text FROM disclaimer WHERE survey_code = ? AND lang = ? ");
            preparedStatement.setString(1, surveyCode);
            preparedStatement.setString(2, lang);

            ResultSet resultSet = preparedStatement.executeQuery();
            return resultSet.next() ? new String(resultSet.getBytes(1)) : EMPTY_STRING;
        }
    }
}
