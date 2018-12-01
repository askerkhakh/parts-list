package com.example.filter;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class FilterImpl implements Filter {

    private final List<FilterItem> items;

    FilterImpl() {
        items = new ArrayList<>();
    }

    @Override
    @NotNull
    public Iterator<FilterItem> iterator() {
        return items.iterator();
    }

    void add(FilterItem filterItem) {
        items.add(filterItem);
    }
}
