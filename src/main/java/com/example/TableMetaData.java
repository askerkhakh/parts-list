package com.example;

public interface TableMetaData {
    FieldMetaData getFieldByName(String fieldName);

    Iterable<FieldMetaData> getFields();
}
