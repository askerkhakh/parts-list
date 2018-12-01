package com.example.sorting;

import java.util.Map;

public class SortInfoFactory {
    public static SortInfo newInstance(Map<String, String[]> reqParameterMap) {
        SortInfoImpl sortInfo = new SortInfoImpl();
        String[] sortField = reqParameterMap.get(SortInfoImpl.SORT_FIELD_QUERY_PARAM);
        String[] sortOrder = reqParameterMap.get(SortInfoImpl.SORT_ORDER_QUERY_PARAM);
        if (sortField != null && sortOrder != null) {
            sortInfo.setSortFieldName(sortField[0]);
            sortInfo.setSortDirection(SortDirection.valueOf(sortOrder[0]));
        }
        return sortInfo;
    }
}
