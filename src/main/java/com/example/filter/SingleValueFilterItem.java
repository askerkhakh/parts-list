package com.example.filter;

import com.example.meta.FieldMetaData;
import org.jetbrains.annotations.Nullable;

public abstract class SingleValueFilterItem<T> extends FilterItemImpl {

    @Nullable
    private T value;

    SingleValueFilterItem(FieldMetaData field) {
        super(field);
    }

    @Override
    public boolean isEmpty() {
        return value == null;
    }

    @Override
    protected String getFilterStringValue() {
        if (value == null)
            return "";
        else
            return value.toString();
    }

    @Nullable
    T getValue() {
        return value;
    }

    void setValue(@Nullable T value) {
        this.value = value;
    }
}
