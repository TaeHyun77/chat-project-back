package com.example.chat.common;

import java.time.format.DateTimeFormatter;

public class DateUtils {
    public static final DateTimeFormatter BASIC_DATE = DateTimeFormatter.ofPattern("yyyyMMdd");

    public static final DateTimeFormatter BASIC_DATE_TIME = DateTimeFormatter.ofPattern("yyyyMMddHHmm");
}
