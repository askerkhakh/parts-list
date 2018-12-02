package com.example.meta;

/**
 * Метаинформация о таблице
 */
public interface TableMetaData {

    /**
     * Возвращает имя таблицы
     */
    String getName();

    /**
     * Возвращает метаинформацию о поле таблицы по имени {@code fieldName}
     */
    FieldMetaData getFieldByName(String fieldName);

    /**
     * Возвращает набор полей
     */
    Iterable<FieldMetaData> getFields();
}
