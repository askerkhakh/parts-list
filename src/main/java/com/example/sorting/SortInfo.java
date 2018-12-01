package com.example.sorting;

import com.example.meta.FieldMetaData;

public interface SortInfo {

    String getQueryStringForField(FieldMetaData fieldMetaData);

    String buildOrderBy();
}
