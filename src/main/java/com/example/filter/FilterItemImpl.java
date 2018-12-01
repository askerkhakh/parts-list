package com.example.filter;

import com.example.Utils;
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
                getInputTagType(), getFilterId(), getName(), getStringValue());
    }

    @Override
    public String getQueryString() {
        return String.format("%s=%s", getName(), getStringValue());
    }

    protected String getStringValue() {
        return "";
    }

    protected String getInputTagType() {
        return "";
    }

    protected String getName() {
        return field.getName();
    }

    protected String getFilterId() {
        return getName();
    }

    protected String getQuotedFieldName() {
        return Utils.quoteString(field.getName());
    }

}
