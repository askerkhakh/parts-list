package com.example.meta;

public class FieldMetaDataImpl implements FieldMetaData {

    private final String name;
    private final FieldType type;

    FieldMetaDataImpl(String name, FieldType type) {
        this.name = name;
        this.type = type;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public FieldType getType() {
        return type;
    }
}
