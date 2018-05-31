package com.sandeep.util;


import com.datastax.driver.core.DataType;

public class StringUtils {
    public static boolean isEmpty(String string) {
        return string == null || string.length() < 1;
    }

    public static String concat(String... tokens) {
        String delimiter = ";";
        StringBuilder builder = new StringBuilder();

        for (String token : tokens) {
            builder.append(token);
            builder.append(delimiter);
        }

        return builder.toString();
    }



}
