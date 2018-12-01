package com.example.filter;

import com.example.meta.FieldMetaData;

public abstract class FilterItemImpl implements FilterItem {

    private FieldMetaData field;

    FilterItemImpl(FieldMetaData field) {
        this.field = field;
    }

    @Override
    public FieldMetaData getFieldMetaData() {
        return field;
    }

    @Override
    public String getLabelTag() {
        return String.format("<label for=\"%s\">%s</label>", getFilterId(), field.getName());
    }

    @Override
    public String getInputTag() {
        return String.format("<input type=\"%s\" id=\"%s\" name=\"%s\" value=\"%s\"/>",
                getInputTagType(), getFilterId(), getFilterName(), getFilterStringValue());
    }

    protected String getFilterStringValue() {
        return "";
    }

    protected String getInputTagType() {
        return "";
    }

    protected String getFilterName() {
        return field.getName();
    }

    protected String getFilterId() {
        return getFilterName();
    }

    protected String getQuotedFieldName() {
        // TODO: оборачивать в кавычки только при необходимости
        return "\"" + field.getName() + "\"";
    }

}
