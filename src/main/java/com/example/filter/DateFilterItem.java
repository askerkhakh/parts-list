package com.example.filter;

import com.example.meta.FieldMetaData;
import org.jetbrains.annotations.Nullable;

import java.sql.Date;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;

import static com.example.Utils.encodeQueryParam;

public class DateFilterItem extends FilterItemImpl {

    private static final String AFTER_DATE_FIELD_SUFFIX = "_after";
    private static final String BEFORE_DATE_FIELD_SUFFIX = "_before";
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("MMM dd, yyyy");
    @Nullable
    private LocalDate afterValue;
    @Nullable
    private LocalDate beforeValue;

    DateFilterItem(FieldMetaData field) {
        super(field);
    }

    static FilterItem ofReqParameterMap(FieldMetaData field, Map<String, String[]> reqParameterMap) {
        DateFilterItem dateFilterItem = new DateFilterItem(field);
        String[] afterValue = reqParameterMap.get(dateFilterItem.getAfterInputName());
        if (afterValue != null && !afterValue[0].isEmpty())
            try {
                dateFilterItem.afterValue = LocalDate.parse(afterValue[0], FORMATTER);
            }
            catch (DateTimeParseException e) {
                // если не удалось разобрать строку, то молча продолжаем работу
            }
        else
            dateFilterItem.afterValue = null;
        String[] beforeValue = reqParameterMap.get(dateFilterItem.getBeforeInputName());
        if (beforeValue != null && !beforeValue[0].isEmpty())
            try {
                dateFilterItem.beforeValue = LocalDate.parse(beforeValue[0], FORMATTER);
            }
            catch (DateTimeParseException e) {
                // если не удалось разобрать строку, то молча продолжаем работу
            }
        else
            dateFilterItem.beforeValue = null;
        return dateFilterItem;
    }

    @Override
    protected String getFilterId() {
        return getAfterInputId();
    }

    private String getAfterInputName() {
        return getFieldMetaData().getName() + AFTER_DATE_FIELD_SUFFIX;
    }

    private String getAfterInputId() {
        return getAfterInputName();
    }

    private String getBeforeInputName() {
        return getFieldMetaData().getName() + BEFORE_DATE_FIELD_SUFFIX;
    }

    private String getBeforeInputId() {
        return getBeforeInputName();
    }

    @Override
    public String getInputTag() {
        return
                String.format("<label for=\"%s\">after</label>\n<input type=\"text\" id=\"%s\" name=\"%s\" value=\"%s\"/>",
                        getAfterInputId(), getAfterInputName(), getAfterInputName(), afterValue == null ? "" : afterValue.format(FORMATTER)
                ) +
                String.format("<label for=\"%s\">before</label>\n<input type=\"text\" id=\"%s\" name=\"%s\" value=\"%s\"/>",
                        getBeforeInputId(), getBeforeInputName(), getBeforeInputName(), beforeValue == null ? "" : beforeValue.format(FORMATTER)
                );
    }

    @Override
    public String getQueryString() {
        String afterQueryString = "";
        if (afterValue != null)
            afterQueryString = String.format("%s=%s", encodeQueryParam(getAfterInputName()), encodeQueryParam(afterValue.format(FORMATTER)));
        String beforeQueryString = "";
        if (beforeValue != null)
            beforeQueryString = String.format("%s=%s", encodeQueryParam(getBeforeInputName()), encodeQueryParam(beforeValue.format(FORMATTER)));
        return String.join("&", afterQueryString, beforeQueryString);
    }

    @Override
    public boolean isEmpty() {
        return afterValue == null || beforeValue == null;
    }

    @Override
    public String buildCondition(List<Object> parameterList) {
        if (afterValue != null)
            parameterList.add(Date.valueOf(afterValue));
        if (beforeValue != null)
            parameterList.add(Date.valueOf(beforeValue));
        return String.format("%s between ? and ?", getQuotedFieldName());
    }
}
