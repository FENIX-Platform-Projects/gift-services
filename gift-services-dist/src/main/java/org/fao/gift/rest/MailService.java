package org.fao.gift.rest;

import org.fao.gift.common.dto.DownloadNotification;
import org.fao.gift.common.dto.MainConfig;
import org.fao.gift.services.DisclaimerLogic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import javax.ejb.Asynchronous;
import javax.ejb.ConcurrencyManagement;
import javax.ejb.Lock;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.Scanner;

import static javax.ejb.ConcurrencyManagementType.CONTAINER;
import static javax.ejb.LockType.READ;

@Startup
@Singleton
@ConcurrencyManagement(CONTAINER)
public class MailService {

    private static final Logger log = LoggerFactory.getLogger(MailService.class);

    private static final String DEFAULT_CONTENT_TYPE = MediaType.TEXT_HTML;

    @Resource(name = "java:/gift_mail")
    private Session session;

    @Inject
    MainConfig config;

    @Inject
    private DisclaimerLogic disclaimerLogic;

    private static String emailTemplate = null;

    @SuppressWarnings("unused")
    public MailService() {
    }

    @PostConstruct
    private void init() {
        log.info("Mail Service Started");
    }

    @PreDestroy
    private void stop() {
        log.info("Mail Service Stopped");
    }

    @Lock(READ)
    @Asynchronous
    public void sendMail(DownloadNotification downloadNotification) {
        final String email = downloadNotification.getEmail();
        final String uid = downloadNotification.getUid();

        log.info("sendMail - START - email: {}, survey {}", email, downloadNotification.getSurveyTitle());

        final String name = downloadNotification.getName() != null ? downloadNotification.getName() : "user";
        final String surveyTitle = downloadNotification.getSurveyTitle() != null ? downloadNotification.getSurveyTitle() : "";
        final String lang = downloadNotification.getLang() != null ? downloadNotification.getLang() : config.get("gift.disclaimer.lang.default");

        String disclaimerContent;
        try {
            disclaimerContent = disclaimerLogic.getDisclaimer(uid, lang);
        } catch (SQLException e) {
            log.error("Cannot retrieve disclaimer {}, {}", uid, lang);
            disclaimerContent = "";
        }

        sendMail(loadDownloadEmailTemplate(), email, name, surveyTitle, disclaimerContent);
    }

    @Lock(READ)
    @Asynchronous
    public void sendMail(String emailTemplate, String emailAddress, String... emailParameters) {
        try {
            Message message = new MimeMessage(session);
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(emailAddress));
            message.setSubject(config.get("gift.mail.subject"));

            String body = MessageFormat.format(emailTemplate, (Object[]) emailParameters);

            message.setContent(body, DEFAULT_CONTENT_TYPE.concat("; charset=utf-8"));
            message.setFrom(InternetAddress.parse(config.get("gift.mail.sender"))[0]);
//            message.setReplyTo(InternetAddress.parse(config.get("gift.mail.replyto")));

            message.saveChanges();
            Transport.send(message);

            log.info("sendMail - DONE - email: {}", emailAddress);

        } catch (MessagingException e) {
            log.error("Cannot send mail", e);
        }
    }


    /**
     * Loads the entire email template file
     *
     * @return the template file config a {@link String} object
     */
    private String loadDownloadEmailTemplate() {
        if (emailTemplate == null) {
            try (InputStream resourceStream = MailService.class.getClassLoader().getResourceAsStream(config.get("gift.mail.template.path"))) {
                if (resourceStream == null) throw new IOException();
                emailTemplate = new Scanner(resourceStream).useDelimiter("\\A").next();

            } catch (IOException e) {
                log.error("Could not find read email template file");
                throw new RuntimeException("Missing email template file");
            }
        }
        return emailTemplate;
    }


}