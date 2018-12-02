package com.example.meta;

/**
 * Метаинформация о поле
 */
public interface FieldMetaData {

    /**
     * Возвращает имя поля
     */
    String getName();

    /**
     * Возвращает тип поля
     */
    FieldType getType();

    /**
     * Отображаемое имя поля
     */
    String getLabel();
}
