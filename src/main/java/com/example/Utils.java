package com.example;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class Utils {

    public static String quoteString(String string) {
        // TODO: оборачивать в кавычки только при необходимости
        return "\"" + string + "\"";
    }

    public static String encodeQueryParam(String queryParam) {
        try {
            return URLEncoder.encode(queryParam, StandardCharsets.UTF_8.toString());
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

}
