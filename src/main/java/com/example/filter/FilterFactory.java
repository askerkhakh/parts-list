package com.example.filter;

import com.example.meta.FieldMetaData;
import com.example.meta.TableMetaData;

import java.util.Map;

/**
 * Фабрика для фильтра, отделяет реализацию фильтра от его пользователей
 */
public class FilterFactory {

    /**
     * Строит информацию о фильтре по метаданным таблицы и массиву параметров запроса
     * @param tableMetaData метаданные таблицы
     * @param reqParameterMap набор параметров
     * @return информация о фильтре
     */
    public static Filter newInstance(TableMetaData tableMetaData, Map<String, String[]> reqParameterMap) {
        FilterImpl filter = new FilterImpl();
        for (FieldMetaData field : tableMetaData.getFields()) {
            filter.add(FilterItemFactory.newInstance(field, reqParameterMap));
        }
        return filter;
    }

}
