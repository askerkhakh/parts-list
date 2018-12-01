package com.example.filter;

import com.example.meta.FieldMetaData;

import java.util.Map;

public class FilterItemFactory {

    public static FilterItem newInstance(FieldMetaData field, Map<String, String[]> reqParameterMap) {
        switch (field.getType()) {
            case INTEGER:
                return IntegerFilterItem.ofReqParameterMap(field, reqParameterMap);
            case STRING:
                return StringFilterItem.ofReqParameterMap(field, reqParameterMap);
            case DATE:
                return DateFilterItem.ofReqParameterMap(field, reqParameterMap);
            default:
                throw new AssertionError(String.format("Фильтрация по полю типа %s", field.getType().toString()));
        }
    }

}
