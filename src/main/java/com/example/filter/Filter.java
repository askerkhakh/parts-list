package com.example.filter;

/**
 * Информация о фильтре
 */
public interface Filter extends Iterable<FilterItem>{
    /**
     * Возвращает информацию о фильтре в виде query части URL
     * @return query-строка
     */
    String toQueryString();
}
