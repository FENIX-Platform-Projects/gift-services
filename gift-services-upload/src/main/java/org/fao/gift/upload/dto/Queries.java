package org.fao.gift.upload.dto;

public enum Queries {

    //CLEAN
    cleanFoodGroups("TRUNCATE TABLE STAGE.FOOD_GROUP"),
    cleanSubjectRaw("TRUNCATE TABLE STAGE.SUBJECT_RAW"),
    cleanConsumptionRaw("TRUNCATE TABLE STAGE.CONSUMPTION_RAW"),
    insertFoodGroups("INSERT INTO STAGE.FOOD_GROUP(GROUP_CODE,SUBGROUP_CODE,FOODEX2_CODE) VALUES (?,?,?)"),

    //VALIDATE
    getUnexistingFoodGroup("SELECT DISTINCT C.FOODEX2_CODE FROM STAGE.CONSUMPTION_RAW C LEFT JOIN STAGE.FOOD_GROUP G ON (C.FOODEX2_CODE = G.FOODEX2_CODE) WHERE SUBGROUP_CODE IS NULL"),
    getSurveyList("SELECT DISTINCT SURVEY_CODE FROM (SELECT DISTINCT SURVEY_CODE FROM STAGE.CONSUMPTION_RAW UNION ALL SELECT DISTINCT SURVEY_CODE FROM STAGE.SUBJECT_RAW) SURVEYS"),

    //PUBLISH
    updateSubject("{ call refresh_subject(?) }"),
    updateConsumption("{ call refresh_consumption(?) }"),
    updateSurveyIndex("{ call refresh_survey_index(?) }"),
//    updateMaster(""),
//    updateMasterAvg(""),

    ;

    private String query;

    Queries(String query) {
        this.query = query;
    }

    public String getQuery() {
        return query;
    }
}
