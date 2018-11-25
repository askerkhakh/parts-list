package com.example.meta;

public interface TableMetaData {

    String getName();

    FieldMetaData getFieldByName(String fieldName);

    Iterable<FieldMetaData> getFields();
}
