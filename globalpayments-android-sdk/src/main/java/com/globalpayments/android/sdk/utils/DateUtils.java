package com.globalpayments.android.sdk.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static com.globalpayments.android.sdk.utils.Strings.EMPTY;
import static com.globalpayments.android.sdk.utils.Utils.isNullOrBlank;

public class DateUtils {
    public static final String ISO_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
    public static final String YYYY_MM_DD = "yyyy-MM-dd";

    public static String getNowDateFormatted() {
        return getNowDateFormatted(ISO_DATE_FORMAT);
    }

    public static String getNowDateFormatted(String dateFormat) {
        return new SimpleDateFormat(dateFormat, Locale.getDefault()).format(new Date());
    }

    public static String getDateISOFormatted(Date date) {
        return getDateFormatted(date, ISO_DATE_FORMAT);
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
}
