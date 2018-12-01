package com.example.filter;

import com.example.meta.FieldMetaData;

import java.util.List;
import java.util.Map;

public class IntegerFilterItem extends SingleValueFilterItem<Integer> {


    IntegerFilterItem(FieldMetaData field) {
        super(field);
    }

    static FilterItem ofReqParameterMap(FieldMetaData field, Map<String, String[]> reqParameterMap) {
        IntegerFilterItem integerFilterItem = new IntegerFilterItem(field);
        String[] value = reqParameterMap.get(integerFilterItem.getName());
        if (value != null && !value[0].isEmpty())
            integerFilterItem.setValue(Integer.parseInt(value[0]));
        else
            integerFilterItem.setValue(null);
        return integerFilterItem;
    }

    @Override
    protected String getInputTagType() {
        return "number";
    }


    @Override
    public String buildCondition(List<Object> parameterList) {
        parameterList.add(getValue());
        return String.format("%s >= ?", getQuotedFieldName());
    }
}
