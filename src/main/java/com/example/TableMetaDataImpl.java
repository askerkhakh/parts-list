package com.example;

import java.util.HashMap;
import java.util.Map;

import static java.util.Objects.requireNonNull;

public class TableMetaDataImpl implements TableMetaData {
    private Map<String, FieldMetaData> fieldsMap;

    TableMetaDataImpl() {
        fieldsMap = new HashMap<>();
    }

    @Override
    public FieldMetaData getFieldByName(String fieldName) {
        return requireNonNull(fieldsMap.get(fieldName));
    }

    @Override
    public Iterable<FieldMetaData> getFields() {
        return fieldsMap.values();
    }

    void add(FieldMetaData field) {
        fieldsMap.put(field.getName(), field);
    }
}
