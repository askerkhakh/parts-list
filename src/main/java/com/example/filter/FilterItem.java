package com.example.filter;

import com.example.meta.FieldMetaData;

import java.util.List;

public interface FilterItem {

    FieldMetaData getFieldMetaData();

    String getLabelTag();

    String getInputTag();

    boolean isEmpty();

    String buildCondition(List<Object> parameterList);

    String getQueryString();
}