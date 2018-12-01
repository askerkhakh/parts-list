package com.example.filter;

import com.example.meta.FieldMetaData;

import java.util.List;
import java.util.Map;

public class StringFilterItem extends SingleValueFilterItem<String> {

    StringFilterItem(FieldMetaData field) {
        super(field);
    }

    static FilterItem ofReqParameterMap(FieldMetaData field, Map<String, String[]> reqParameterMap) {
        StringFilterItem stringFilterItem = new StringFilterItem(field);
        String[] value = reqParameterMap.get(stringFilterItem.getName());
        if (value != null && !value[0].isEmpty())
            stringFilterItem.setValue(value[0]);
        else
            stringFilterItem.setValue(null);
        return stringFilterItem;
    }

    @Override
    protected String getInputTagType() {
        return "text";
    }

    @Override
    public String buildCondition(List<Object> parameterList) {
        // По условиям задачи не совсем понятно, что имеется в виду под "like" criteria.
        // Можно было бы поддержать звёздочку и знак вопроса, но чтобы не углубляться в возню с деталями сделаю здесь
        // простой вариант - ввёдённая в фильтре строка ищется как подстрока в поле таблицы. В любом случае, построение
        // условия инкапуслировано здесь, поэтому доработать можно в любой момент.
        parameterList.add(String.format("%%%s%%", getValue()));
        return String.format("%s like ?", getQuotedFieldName());
    }
}
