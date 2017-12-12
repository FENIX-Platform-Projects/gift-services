package org.fao.gift.commons.dto;

public class Disclaimer {

    private String UID;
    private String text;
    private String lang;


    public Disclaimer() {
    }

    public Disclaimer(String UID, String text, String lang) {
        this.UID = UID;
        this.text = text;
        this.lang = lang;
    }

    public String getUID() {
        return UID;
    }

    public void setUID(String UID) {
        this.UID = UID;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    @Override
    public String toString() {
        return "Disclaimer{" +
                "UID='" + UID + '\'' +
                ", text='" + text + '\'' +
                ", lang='" + lang + '\'' +
                '}';
    }
}
