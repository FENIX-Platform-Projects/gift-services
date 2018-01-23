package org.fao.gift.dao.impl;

import org.fao.gift.dao.Dao;
import org.fao.gift.common.dto.MainConfig;
import org.fao.gift.common.dto.User;
import org.fao.gift.common.dto.UserRole;
import org.fao.gift.common.utils.JwtUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.persistence.EntityExistsException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class UserDao extends Dao {

    private static final Logger log = LoggerFactory.getLogger(UserDao.class);

    public static final String TABLE_NAME = "\"user\"";
    private static final String ID_COLUMN = "forum_id";

    @Inject
    MainConfig config;


    /**
     * Persist User info
     *
     * @param forumId     the user's id provided by the forum
     * @param name        the user's full name
     * @param username    the user's username in the platform
     * @param role        the user's role on GIFT site
     * @param institution the user's Institution
     * @param email       the user's email address
     * @throws EntityExistsException in case a user already exists with given UID
     * @throws SQLException          if the user already exists or there are problems with the DB
     */
    public void create(long forumId, String name, String username, UserRole role, String institution, String email) throws SQLException {
        // JWT: the JSON Web Token that identifies the User on the forum
        String jwt = generateJwt(forumId, username);
        log.info("Generated JWT: {}", jwt);

        // Test uniqueness for better feedback to the user
        if (find(forumId) != null) {
            throw new EntityExistsException();
        }

        try (Connection connection = getConnection()) {
            //language=PostgreSQL
            String insertQuery = "INSERT INTO " + TABLE_NAME + " (forum_id, name, username, role, institution, email, jwt) VALUES (?, ?, ?, ?, ?, ?, ?)";

            PreparedStatement insertStatement = connection.prepareStatement(insertQuery);

            insertStatement.setLong(1, forumId);
            insertStatement.setString(2, name);
            insertStatement.setString(3, username);
            insertStatement.setString(4, role.toString().toLowerCase());
            insertStatement.setString(5, institution);
            insertStatement.setString(6, email);
            insertStatement.setString(7, jwt);

            int affectedRows = insertStatement.executeUpdate();
            log.info("create - Affected {} rows", affectedRows);

            connection.commit();
        }
    }

    /**
     * Retrieve a user's info, given his username
     *
     * @param username the username of the User of interest
     * @return a {@link User} object, with user info
     * @throws SQLException in case of problems with the DB
     */
    public User find(final String username) throws SQLException {
        try (Connection connection = getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM " + TABLE_NAME + " WHERE username = ? ");
            preparedStatement.setString(1, username);

            ResultSet resultSet = preparedStatement.executeQuery();
            return resultSet.next() ? extractUser(resultSet) : null;
        }
    }

    /**
     * Retrieve a user's info, given his forumId
     *
     * @param forumId the forumId of the User of interest
     * @return a {@link User} object, with user info
     * @throws SQLException in case of problems with the DB
     */
    public User find(final long forumId) throws SQLException {
        try (Connection connection = getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM " + TABLE_NAME + " WHERE " + ID_COLUMN + " = ? ");
            preparedStatement.setLong(1, forumId);

            ResultSet resultSet = preparedStatement.executeQuery();
            return resultSet.next() ? extractUser(resultSet) : null;
        }
    }

    /**
     * Retrieves all the users info
     *
     * @return a list of existing {@link User}
     * @throws SQLException in case of problems with the DB
     */
    public List<User> findAll() throws SQLException {
        try (Connection connection = getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM " + TABLE_NAME);
            ResultSet resultSet = preparedStatement.executeQuery();

            List<User> users = new ArrayList<>();
            while (resultSet.next()) {
                users.add(extractUser(resultSet));
            }
            return users;
        }
    }


    /**
     * Extract info of a single user from the given ResultSet
     *
     * @param rs the ResultSet, containing info for a single user
     * @return a User with all available user info
     * @throws SQLException in case of problems with the DB
     */
    private static User extractUser(ResultSet rs) throws SQLException {
        return new User(
                rs.getLong("forum_id"),
                rs.getString("name"),
                rs.getString("username"),
                UserRole.valueOf(rs.getString("role").toUpperCase()),
                rs.getString("institution"),
                rs.getString("email"),
                rs.getString("jwt")
        );
    }

    /**
     * Generate JWT based on user's id and username
     *
     * @param id       the user's forum ID
     * @param username the user's forum username
     * @return the corresponding Json Web Token for forum authentication
     */
    private String generateJwt(long id, String username) {
        HashMap<String, Object> payload = new HashMap<>();
        payload.put("id", id);
        payload.put("username", username);

        return JwtUtils.generateJwt(payload, config.get("gift.forum.jwt.secret"));
    }
}
