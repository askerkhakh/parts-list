package com.example.sorting;

import com.example.Utils;
import com.example.meta.FieldMetaData;
import org.jetbrains.annotations.Nullable;

import static com.example.Utils.encodeQueryParam;

class SortInfoImpl implements SortInfo {

    static final String SORT_FIELD_QUERY_PARAM = "sortField";
    static final String SORT_ORDER_QUERY_PARAM = "sortOrder";
    @Nullable
    private String sortFieldName;
    @Nullable
    private SortDirection sortDirection;

    @Override
    public String getQueryStringForField(FieldMetaData fieldMetaData) {
        SortDirection direction = SortDirection.ASCENDING;
        String field = fieldMetaData.getName();
        if (sortFieldName != null && sortDirection != null)
            if (field.equals(sortFieldName))
                direction = sortDirection.toggle();
        return String.format("%s=%s&%s=%s", SORT_FIELD_QUERY_PARAM, encodeQueryParam(field), SORT_ORDER_QUERY_PARAM, direction);
    }

    @Override
    public String buildOrderBy() {
        if (sortFieldName == null || sortDirection == null)
            return "";
        String result = Utils.quoteString(sortFieldName);
        if (sortDirection == SortDirection.ASCENDING)
            result += " asc";
        else
            result += " desc";
        return result;
    }

    void setSortFieldName(@Nullable String sortFieldName) {
        this.sortFieldName = sortFieldName;
    }

    void setSortDirection(@Nullable SortDirection sortDirection) {
        this.sortDirection = sortDirection;
    }
}
