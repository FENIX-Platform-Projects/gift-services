package org.fao.gift.dao.impl;

import org.fao.gift.dao.Dao;
import org.fao.gift.commons.dto.MainConfig;
import org.fao.gift.commons.dto.Survey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.persistence.EntityExistsException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SurveyDao extends Dao {

    private static final Logger log = LoggerFactory.getLogger(SurveyDao.class);

    private static final String TABLE_NAME = "\"survey\"";
    private static final String ID_COLUMN = "survey_id";

    @Inject
    MainConfig config;


    /**
     * Persist Survey info
     *
     * @param surveyId       the survey UID
     * @param categoryId     the forum category ID
     * @param userId         the forum user ID (the user who submits data)
     * @param defaultTopicId the topic ID for system messages
     * @throws EntityExistsException in case a survey already exists with given UID
     * @throws SQLException          in case of problems with the DB
     */
    public void create(String surveyId, long categoryId, long userId, long defaultTopicId) throws SQLException {
        if (find(surveyId) != null) {
            throw new EntityExistsException();
        }

        try (Connection connection = getConnection()) {
            //language=PostgreSQL
            String insertQuery = "INSERT INTO " + TABLE_NAME + " (survey_id, category_id, user_id, default_topic_id) VALUES (?, ?, ?, ?)";

            PreparedStatement insertStatement = connection.prepareStatement(insertQuery);

            insertStatement.setString(1, surveyId);
            insertStatement.setLong(2, categoryId);
            insertStatement.setLong(3, userId);
            insertStatement.setLong(4, defaultTopicId);

            int affectedRows = insertStatement.executeUpdate();
            log.info("create - Affected {} rows", affectedRows);

            connection.commit();
        }
    }


    /**
     * Retrieve a survey's info, given its UID
     *
     * @param surveyId the UID of the Survey of interest
     * @return a {@link Survey} object, with survey info
     * @throws SQLException in case of problems with the DB
     */
    public Survey find(final String surveyId) throws SQLException {
        try (Connection connection = getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM " + TABLE_NAME + " WHERE " + ID_COLUMN + " = ? ");
            preparedStatement.setString(1, surveyId);

            ResultSet resultSet = preparedStatement.executeQuery();
            return resultSet.next() ? extractSurvey(resultSet) : null;
        }
    }


    /**
     * Retrieves all the survey info
     *
     * @return a list of existing {@link Survey}
     * @throws SQLException in case of problems with the DB
     */
    public List<Survey> findAll() throws SQLException {
        try (Connection connection = getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM " + TABLE_NAME);
            ResultSet resultSet = preparedStatement.executeQuery();

            List<Survey> surveys = new ArrayList<>();
            while (resultSet.next()) {
                surveys.add(extractSurvey(resultSet));
            }
            return surveys;
        }
    }

    /**
     * Retrieve the list of surveys assigned to the given user, given his forum ID
     *
     * @param forumId the user forum ID
     * @return a list of {@link Survey} object
     * @throws SQLException in case of problems with the DB
     */
    public List<Survey> findSurveysByUser(final long forumId) throws SQLException {
        try (Connection connection = getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM " + TABLE_NAME + " WHERE user_id = ?");
            preparedStatement.setLong(1, forumId);

            ResultSet resultSet = preparedStatement.executeQuery();

            List<Survey> surveys = new ArrayList<>();
            while (resultSet.next()) {
                surveys.add(extractSurvey(resultSet));
            }
            return surveys;
        }
    }

    /**
     * Retrieve the list of surveys assigned to the given user, given his username
     *
     * @param username the user's platform username
     * @return a list of {@link Survey} object
     * @throws SQLException in case of problems with the DB
     */
    public List<Survey> findSurveysByUser(final String username) throws SQLException {
        try (Connection connection = getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement("" +
                    "SELECT * FROM " + TABLE_NAME + " s, " + UserDao.TABLE_NAME + " u, " +
                    "WHERE s.user_id = u.forum_id " +
                    "AND u.username = ? ");
            preparedStatement.setString(1, username);

            ResultSet resultSet = preparedStatement.executeQuery();

            List<Survey> surveys = new ArrayList<>();
            while (resultSet.next()) {
                surveys.add(extractSurvey(resultSet));
            }
            return surveys;
        }
    }


    /**
     * Extract info of a single survey from the given ResultSet
     *
     * @param rs the ResultSet, containing info for a single survey
     * @return a Survey with all available survey info
     * @throws SQLException in case of problems with the DB
     */
    private static Survey extractSurvey(ResultSet rs) throws SQLException {
        return new Survey(
                rs.getString("survey_id"),
                rs.getLong("category_id"),
                rs.getLong("user_id"),
                rs.getLong("default_topic_id")
        );
    }
}
