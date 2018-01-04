package org.fao.gift.config.email;

/**
 * Utility class to keep track of managed email templates.
 * Add an entry for any template you need to load in your application.
 */
public enum MailTemplate {

    ds_download("FAO/WHO GIFT microdata download"),
    md_creation("FAO/WHO GIFT metadata creation"),
    md_download("FAO/WHO GIFT metadata download"),
    md_update("FAO/WHO GIFT metadata update");

    private String mailSubject;

    MailTemplate(String mailSubject) {
        this.mailSubject = mailSubject;
    }

    public String getMailSubject() {
        return mailSubject;
    }
}
