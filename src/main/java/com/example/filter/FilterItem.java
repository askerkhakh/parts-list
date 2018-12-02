package com.example.filter;

import com.example.meta.FieldMetaData;

import java.util.List;

/**
 * Элемент фильтра, соответствующий полю таблицы
 */
public interface FilterItem {

    /**
     * Возвращает метаинформацию о поле
     */
    FieldMetaData getFieldMetaData();

    /**
     * Возвращает тег для метки элемента фильтра
     */
    String getLabelTag();

    /**
     * Возвращает тег для поля ввода элемента фильтра
     */
    String getInputTag();

    /**
     * Признак пустоты элемента фильтра
     * @return {@code true}, если не указано значение для фильтрации
     */
    boolean isEmpty();

    /**
     * Строит строку sql-условия фильтрации для элемента фильтра и добавляет значение, по которому будет выполняться
     * фильтрация, в список параметров
     * @param parameterList список значений параметров
     * @return sql-строка условия фильтрации
     */
    String buildCondition(List<Object> parameterList);

    /**
     * Возвращает информацию о элементе фильтра в виде query части URL
     * @return query-строка
     */
    String getQueryString();
}