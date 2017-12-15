package org.fao.gift.config.email;

/**
 * Utility class to keep track of managed email templates.
 * Add an entry for any template you need to load in your application.
 */
public enum MailTemplate {

    DS_DOWNLOAD("FAO/WHO GIFT microdata download"),
    MD_CREATION("FAO/WHO GIFT metadata creation"),
    MD_DOWNLOAD("FAO/WHO GIFT metadata download"),
    MD_UPDATE("FAO/WHO GIFT metadata update");

    private String mailSubject;

    MailTemplate(String mailSubject) {
        this.mailSubject = mailSubject;
    }

    public String getMailSubject() {
        return mailSubject;
    }
}
