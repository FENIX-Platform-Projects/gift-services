package org.fao.gift.services;

import org.fao.gift.dao.impl.SurveyDao;
import org.fao.gift.dao.impl.UserDao;
import org.fao.gift.common.dto.Survey;
import org.fao.gift.common.dto.User;
import org.fao.gift.common.dto.UserRole;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.sql.SQLException;
import java.util.List;

public class UserLogic {

    private static final Logger log = LoggerFactory.getLogger(UserLogic.class);
    private static final String MISSING_FIELD_MESSAGE = " is mandatory";

    @Inject
    private UserDao userDao;

    @Inject
    private SurveyDao surveyDao;

    public User getUser(final String username) throws SQLException {
        log.info("getUser - START - {}", username);
        if (username == null || username.isEmpty()) missing("Username");

        User foundUser = userDao.find(normalize(username));
        log.info("getUser - END - found: {}", foundUser);
        return foundUser;
    }

    public void create(long forumId, String name, String username, UserRole role, String institution, String email) throws SQLException {
        if (forumId == 0) missing("Forum ID");
        if (name == null || name.isEmpty()) missing("Name");
        if (username == null || username.isEmpty()) missing("Username");
        if (role == null) missing("Role");
        if (institution == null || institution.isEmpty()) missing("Institution");
        if (email == null || email.isEmpty()) missing("E-mail");

        userDao.create(forumId, name, normalize(username), role, institution, email);
    }

    public List<Survey> getUserSurveys(final long forumId) throws SQLException {
        return surveyDao.findSurveysByUser(forumId);
    }

    public List<Survey> getUserSurveys(final String username) throws SQLException {
        return surveyDao.findSurveysByUser(normalize(username));
    }

    private static void missing(String s) {
        throw new IllegalArgumentException(s.concat(MISSING_FIELD_MESSAGE));
    }

    public static void normalizeUsername(User user) {
        user.setUsername(normalize(user.getUsername()));
    }

    public static String normalize(String s) {
        return s.replaceAll("[@./]", "_");
    }
}
