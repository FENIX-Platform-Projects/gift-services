package org.fao.gift.services;

import org.fao.gift.dao.impl.DisclaimerDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.sql.SQLException;

public class DisclaimerLogic {

    private static final Logger log = LoggerFactory.getLogger(DisclaimerLogic.class);

    private static final String DEFAULT_LANG = "en";

    @Inject
    private DisclaimerDao disclaimerDao;

    public String getDisclaimer(final String surveyCode, String lang) throws SQLException {
        log.info("getDisclaimer - START - {} {}", surveyCode, lang);
        if (surveyCode == null || surveyCode.isEmpty()) throw new IllegalArgumentException("Survey Code is mandatory");
        if (lang == null || lang.isEmpty()) lang = DEFAULT_LANG;

        String foundDisclaimer = disclaimerDao.findBySurvey(surveyCode, lang);
        log.info("getDisclaimer - STOP - found: {}", foundDisclaimer != null && !foundDisclaimer.isEmpty());
        return foundDisclaimer;
    }

    public void updateDisclaimer(final String surveyCode, final String text, String lang) throws SQLException {
        log.info("updateDisclaimer - START - {} {}", surveyCode, lang);
        if (surveyCode == null || surveyCode.isEmpty()) throw new IllegalArgumentException("Survey Code is mandatory");
        if (text == null || text.isEmpty()) throw new IllegalArgumentException("Disclaimer content cannot be empty");
        if (lang == null || lang.isEmpty()) lang = DEFAULT_LANG;

        disclaimerDao.updateBySurvey(surveyCode, text, lang);
    }

}
