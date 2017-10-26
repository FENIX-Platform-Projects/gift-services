package org.fao.gift.dto.search;

import org.fao.fenix.commons.find.dto.filter.*;
import org.fao.fenix.commons.find.dto.type.FieldFilterType;

import java.util.Collection;
import java.util.InvalidPropertiesFormatException;
import java.util.LinkedList;

public class StatisticsParameters {

    //Geographic
    public CodesFilter countries = null;
    //Survey
    public CodesFilter referenceArea = null;
    public CodesFilter coverageSector = null;
    public TimeFilter year = null;
    public CodesFilter confidentialityStatus = null;

    //Pupulation
    public String gender;
    public Collection<String> special_condition;
    public NumberFilter age_year;
    public NumberFilter age_month;
    //Food
    public Collection<String> foodex2_code;


    public StatisticsParameters() {
    }

    public StatisticsParameters(StandardFilter fenixFilter) throws InvalidPropertiesFormatException {
        init(fenixFilter);
    }

    public void init(StandardFilter fenixFilter) throws InvalidPropertiesFormatException {
        reset();
        if (fenixFilter != null) {
            //Metadata fields
            countries = extractCodesFilter(fenixFilter, "meContent.seCoverage.coverageGeographic");
            referenceArea = extractCodesFilter(fenixFilter, "meContent.seReferencePopulation.referenceArea");
            coverageSector = extractCodesFilter(fenixFilter, "meContent.seCoverage.coverageSectors");
            year = extractTimeFilter(fenixFilter, "meContent.seCoverage.coverageTime");
            confidentialityStatus = extractCodesFilter(fenixFilter, "meAccessibility.seConfidentiality.confidentialityStatus");
            //Data fields
            gender = extractCode(fenixFilter, "gender");
            special_condition = extractCodes(fenixFilter, "special_condition");
            age_year = extractNumberFilter(fenixFilter, "age_year");
            age_month = extractNumberFilter(fenixFilter, "age_month");
            foodex2_code = extractCodes(fenixFilter, "foodex2_code");
        }
    }

    public void reset() {
        //Metadata fields
        countries = null;
        referenceArea = null;
        coverageSector = null;
        year = null;
        confidentialityStatus = null;
        //Data fields
        gender = null;
        special_condition = null;
        age_year = null;
        age_month = null;
        foodex2_code = null;
    }


    //Utils
    private NumberFilter extractNumberFilter(StandardFilter fenixFilter, String fieldName) throws InvalidPropertiesFormatException {
        FieldFilter fieldFilter = fenixFilter.get(fieldName);
        if (fieldFilter != null) {
            if (fieldFilter.retrieveFilterType() != FieldFilterType.number)
                throw new InvalidPropertiesFormatException(fieldName + " must be a number type filter");
            if (fieldFilter.number.size() > 1)
                throw new InvalidPropertiesFormatException(fieldName + " must be a single value number type filter");
            return fieldFilter.number.size() > 0 ? fieldFilter.number.iterator().next() : null;
        }
        return null;
    }

    private TimeFilter extractTimeFilter(StandardFilter fenixFilter, String fieldName) throws InvalidPropertiesFormatException {
        FieldFilter fieldFilter = fenixFilter.get(fieldName);
        if (fieldFilter != null) {
            if (fieldFilter.retrieveFilterType() != FieldFilterType.time)
                throw new InvalidPropertiesFormatException(fieldName + " must be a time type filter");
            if (fieldFilter.time.size() > 1)
                throw new InvalidPropertiesFormatException(fieldName + " must be a single value time type filter");
            return fieldFilter.time.size() > 0 ? fieldFilter.time.iterator().next() : null;
        }
        return null;
    }


    private CodesFilter extractCodesFilter(StandardFilter fenixFilter, String fieldName) throws InvalidPropertiesFormatException {
        FieldFilter fieldFilter = fenixFilter.get(fieldName);
        if (fieldFilter != null) {
            if (fieldFilter.retrieveFilterType() != FieldFilterType.code)
                throw new InvalidPropertiesFormatException(fieldName + " must be a code type filter");
            if (fieldFilter.codes.size() > 1)
                throw new InvalidPropertiesFormatException(fieldName + " must be a single code list value");
            return fieldFilter.codes.size() > 0 ? fieldFilter.codes.iterator().next() : null;
        }
        return null;
    }

    private String extractCode(StandardFilter fenixFilter, String fieldName) throws InvalidPropertiesFormatException {
        Collection<String> value = extractCodes(fenixFilter, fieldName);
        if (value != null && value.size() > 1)
            throw new InvalidPropertiesFormatException(fieldName + " must be a single value code type filter");
        return value != null && value.size() == 1 ? value.iterator().next() : null;
    }

    private Collection<String> extractCodes(StandardFilter fenixFilter, String fieldName) throws InvalidPropertiesFormatException {
        Collection<String> value = new LinkedList<>();
        FieldFilter fieldFilter = fenixFilter.get(fieldName);
        if (fieldFilter != null) {
            if (fieldFilter.retrieveFilterType() != FieldFilterType.code)
                throw new InvalidPropertiesFormatException(fieldName + " must be a code type filter");

            for (CodesFilter codesFilter : fieldFilter.codes)
                if (codesFilter.codes != null)
                    value.addAll(codesFilter.codes);
        }
        return value.size() > 0 ? value : null;
    }


}
