package org.fao.gift.dto;

public class Survey {

    private String surveyId;
    private long categoryId;
    private long userId;
    private long defaultTopicId;

    public Survey() {
    }

    public Survey(String surveyId, long categoryId, long userId, long defaultTopicId) {
        this.surveyId = surveyId;
        this.categoryId = categoryId;
        this.userId = userId;
        this.defaultTopicId = defaultTopicId;
    }

    public String getSurveyId() {
        return surveyId;
    }

    public void setSurveyId(String surveyId) {
        this.surveyId = surveyId;
    }

    public long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(long categoryId) {
        this.categoryId = categoryId;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public long getDefaultTopicId() {
        return defaultTopicId;
    }

    public void setDefaultTopicId(long defaultTopicId) {
        this.defaultTopicId = defaultTopicId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Survey survey = (Survey) o;

        if (categoryId != survey.categoryId) return false;
        if (userId != survey.userId) return false;
        if (defaultTopicId != survey.defaultTopicId) return false;
        return surveyId != null ? surveyId.equals(survey.surveyId) : survey.surveyId == null;
    }

    @Override
    public int hashCode() {
        int result = surveyId != null ? surveyId.hashCode() : 0;
        result = 31 * result + (int) (categoryId ^ (categoryId >>> 32));
        result = 31 * result + (int) (userId ^ (userId >>> 32));
        result = 31 * result + (int) (defaultTopicId ^ (defaultTopicId >>> 32));
        return result;
    }

    @Override
    public String toString() {
        return "Survey{" +
                "surveyId='" + surveyId + '\'' +
                ", categoryId=" + categoryId +
                ", userId=" + userId +
                ", defaultTopicId=" + defaultTopicId +
                '}';
    }
}
