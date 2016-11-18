package org.fao.gift.upload.dto;

public enum MetadataTemplates {
    //gift_process_default(true, true),
    //gift_process_daily_avg(true, false),
    //gift_process_total_subgroup_consumption(true, false),

    gift_process_total_food_consumption(true, false),
    gift_process_total_weighted_food_consumption(true, false),
    gift_process_total_round_food_consumption(true, false),
    gift_process_total_round_weighted_food_consumption(true, false),
    ;

    public boolean bySurvey, byItem;
    MetadataTemplates(boolean bySurvey, boolean byItem) {
        this.bySurvey = bySurvey;
        this.byItem = byItem;
    }
}
