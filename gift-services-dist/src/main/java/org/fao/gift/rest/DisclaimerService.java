package org.fao.gift.rest;

import org.fao.gift.dto.Disclaimer;
import org.fao.gift.dto.DownloadNotification;
import org.fao.gift.rest.spi.DisclaimerSpi;
import org.fao.gift.services.CaptchaValidator;
import org.fao.gift.services.DisclaimerLogic;
import org.fao.gift.services.MailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;
import java.sql.SQLException;


@Path("disclaimer")
public class DisclaimerService implements DisclaimerSpi {

    private static final Logger log = LoggerFactory.getLogger(DisclaimerService.class);

    @Inject
    private MailService mailService;

    @Inject
    private DisclaimerLogic disclaimerLogic;

    @Inject
    private CaptchaValidator captchaValidator;


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

    @Override
    public Response notifyClient(DownloadNotification downloadNotification) {
        log.info("notifyClient - START - {}", downloadNotification);
        if (downloadNotification.getCaptchaResponse() == null || downloadNotification.getCaptchaResponse().isEmpty()) {
            return Response.status(Response.Status.FORBIDDEN).build();
        }
        if (downloadNotification.getEmail() == null || downloadNotification.getUid() == null) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        // reCaptcha Validation
        boolean verify = captchaValidator.verify(downloadNotification.getCaptchaResponse());
        if (!verify) {
            return Response.status(Response.Status.FORBIDDEN).build();
        }

        mailService.sendMail(downloadNotification);
        return Response.ok().build();
    }
}