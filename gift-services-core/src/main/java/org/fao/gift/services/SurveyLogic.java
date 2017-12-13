package org.fao.gift.services;

import org.fao.gift.dao.impl.SurveyDao;
import org.fao.gift.common.dto.Survey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.sql.SQLException;

public class SurveyLogic {

    private static final Logger log = LoggerFactory.getLogger(SurveyLogic.class);
    private static final String MISSING_FIELD_MESSAGE = " is mandatory";

    @Inject
    private SurveyDao surveyDao;

    public Survey getSurvey(final String surveyId) throws SQLException {
        log.info("getSurvey - START - {}", surveyId);
        if (surveyId == null || surveyId.isEmpty()) missing("Survey UID");

        Survey foundSurvey = surveyDao.find(surveyId);
        log.info("getSurvey - END - found: {}", foundSurvey);
        return foundSurvey;
    }


    public void create(String surveyId, long categoryId, long userId, long defaultTopicId) throws SQLException {
        if (surveyId == null || surveyId.isEmpty()) missing("Survey UID");
        if (categoryId <= 0) missing("Category ID");
        if (userId <= 0) missing("User ID");
        if (defaultTopicId <= 0) missing("Default Topic ID");

        surveyDao.create(surveyId, categoryId, userId, defaultTopicId);
    }

    private static void missing(String s) {
        throw new IllegalArgumentException(s.concat(MISSING_FIELD_MESSAGE));
    }
}
