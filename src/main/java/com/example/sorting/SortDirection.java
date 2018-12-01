package com.example.sorting;

public enum SortDirection {
    ASCENDING, DESCENDING;

    public SortDirection toggle() {
        if (this == ASCENDING)
            return DESCENDING;
        else
            return ASCENDING;
    }
}
