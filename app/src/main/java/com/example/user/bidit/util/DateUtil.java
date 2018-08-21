package com.example.user.bidit.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtil {
    public static String formatDateToLongStyle(Date date) {
        DateFormat format = SimpleDateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.SHORT);
        return format.format(date);
    }
}
