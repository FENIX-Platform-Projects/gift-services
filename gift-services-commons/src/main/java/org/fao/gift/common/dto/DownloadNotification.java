package org.fao.gift.common.dto;

@SuppressWarnings("unused")
public class DownloadNotification {

    private String captchaResponse;
    private String name;
    private String surveyTitle;
    private String email;
    private String uid;
    private String lang;

    public DownloadNotification() {
    }

    public DownloadNotification(String captchaResponse, String name, String surveyTitle, String email, String uid, String lang) {
        this.captchaResponse = captchaResponse;
        this.name = name;
        this.surveyTitle = surveyTitle;
        this.email = email;
        this.uid = uid;
        this.lang = lang;
    }

    public String getCaptchaResponse() {
        return captchaResponse;
    }

    public void setCaptchaResponse(String captchaResponse) {
        this.captchaResponse = captchaResponse;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurveyTitle() {
        return surveyTitle;
    }

    public void setSurveyTitle(String surveyTitle) {
        this.surveyTitle = surveyTitle;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    @Override
    public String toString() {
        return "DownloadNotification{" +
                "captchaResponse='" + captchaResponse + '\'' +
                ", name='" + name + '\'' +
                ", surveyTitle='" + surveyTitle + '\'' +
                ", email='" + email + '\'' +
                ", uid='" + uid + '\'' +
                ", lang='" + lang + '\'' +
                '}';
    }

}
