package com.example;

public class FieldMetaDataImpl implements FieldMetaData {

    private final String name;
    private final int type;

    FieldMetaDataImpl(String name, int type) {
        this.name = name;
        this.type = type;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public int getType() {
        return type;
    }
}
