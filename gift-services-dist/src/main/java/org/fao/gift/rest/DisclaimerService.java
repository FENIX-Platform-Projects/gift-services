package org.fao.gift.rest;

import org.fao.gift.dto.Disclaimer;
import org.fao.gift.rest.spi.DisclaimerSpi;
import org.fao.gift.services.DisclaimerLogic;

import javax.inject.Inject;
import javax.ws.rs.Path;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;


@Path("disclaimer")
public class DisclaimerService implements DisclaimerSpi {

    private static final Logger log = LoggerFactory.getLogger(DisclaimerService.class);

    @Inject
    private DisclaimerLogic disclaimerLogic;


    @Override
    public String getDisclaimer(String surveyCode, String lang) throws Exception {
        log.info("getDisclaimer - {}, {}", surveyCode, lang);
        return disclaimerLogic.getDisclaimer(surveyCode, lang);
    }

    @Override
    public String updateDisclaimer(Disclaimer disclaimer) throws SQLException {
        log.info("updateDisclaimer - START - disclaimer: {}", disclaimer);
        String output = "OK";

        disclaimerLogic.updateDisclaimer(disclaimer.getUID(), disclaimer.getText(), disclaimer.getLang());

        log.info("updateDisclaimer - END - output: {}", output);
        return output;
    }
}