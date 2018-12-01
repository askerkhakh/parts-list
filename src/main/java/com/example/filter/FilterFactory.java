package com.example.filter;

import com.example.meta.FieldMetaData;
import com.example.meta.TableMetaData;

import java.util.Map;

public class FilterFactory {

    public static Filter newInstance(TableMetaData tableMetaData, Map<String, String[]> reqParameterMap) {
        FilterImpl filter = new FilterImpl();
        for (FieldMetaData field : tableMetaData.getFields()) {
            filter.add(FilterItemFactory.newInstance(field, reqParameterMap));
        }
        return filter;
    }

}
