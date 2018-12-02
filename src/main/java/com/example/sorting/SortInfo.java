package com.example.sorting;

import com.example.meta.FieldMetaData;

/**
 * Информация о сортировке
 */
public interface SortInfo {

    /**
     * Возвращает query-часть url для поля {@code fieldMetaData} с учётом того, по какому полю выполнена сортировка сейчас.
     */
    String getQueryStringForField(FieldMetaData fieldMetaData);

    /**
     * Возвращает sql-строку для блока order by
     */
    String buildOrderBy();
}
