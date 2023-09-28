package com.globalpayments.android.sdk.utils;

import static com.globalpayments.android.sdk.utils.Strings.EMPTY;
import static com.globalpayments.android.sdk.utils.Utils.isNullOrBlank;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DateUtils {
    public static final String ISO_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
    public static final String ISO_DATE_FORMAT_2 = "yyyy-MM-dd'T'HH:mm:ss";
    public static final String YYYY_MM_DD = "yyyy-MM-dd";

    public static String getNowDateFormatted(Date date) {
        return getNowDateFormatted(ISO_DATE_FORMAT, date);
    }

    public static String getNowDateFormatted(String dateFormat, Date date) {
        return new SimpleDateFormat(dateFormat, Locale.getDefault()).format(date);
    }

    public static String getDateISOFormatted(Date date) {
        return getDateFormatted(date, ISO_DATE_FORMAT_2);
    }

    public static String getDateFormatted(Date date, String format) {
        if (date == null || isNullOrBlank(format)) {
            return EMPTY;
        } else {
            return new SimpleDateFormat(format, Locale.getDefault()).format(date);
        }
    }

    public static Date parseDate(String dateFormat, String dateString) {
        try {
            return new SimpleDateFormat(dateFormat, Locale.getDefault()).parse(dateString);
        } catch (ParseException e) {
            return null;
        }
    }

    public static boolean isValidDate(String dateFormat, String dateString) {
        return parseDate(dateFormat, dateString) != null;
    }

    public static Date addDays(Date date, int days) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DATE, days);
        return cal.getTime();
    }
}
