package com.globalpayments.android.sdk.utils;

import com.global.api.entities.exceptions.GatewayException;

import java.math.BigDecimal;

import static com.globalpayments.android.sdk.utils.Strings.EMPTY;

public class Utils {
    public static boolean isNullOrBlank(String value) {
        return isNull(value) || value.trim().equals(EMPTY);
    }

    public static boolean isNotNullOrBlank(String value) {
        return !isNullOrBlank(value);
    }

    public static boolean isNull(Object object) {
        return object == null;
    }

    public static boolean isNotNull(Object object) {
        return object != null;
    }

    public static boolean areAllNotNull(Object... objects) {
        for (Object object : objects) {
            if (isNull(object)) {
                return false;
            }
        }
        return true;
    }

    public static String getAmount(BigDecimal amount) {
        if (isNotNull(amount)) {
            return String.valueOf(amount.divide(new BigDecimal(100)));
        }
        return EMPTY;
    }

    public static Integer safeParseInt(String string) {
        Integer integer = null;

        try {
            if (isNotNullOrBlank(string)) {
                integer = Integer.valueOf(string);
            }
        } catch (NumberFormatException ignored) {
        }

        return integer;
    }

    public static BigDecimal safeParseBigDecimal(String string) {
        BigDecimal bigDecimal = null;

        try {
            if (isNotNullOrBlank(string)) {
                bigDecimal = new BigDecimal(string);
            }
        } catch (NumberFormatException ignored) {
        }

        return bigDecimal;
    }

    public static String safeParseBigDecimal(BigDecimal bigDecimal) {
        return (bigDecimal == null) ? EMPTY : bigDecimal.toString();
    }

    public static String getExceptionDescription(Exception exception) {
        StringBuilder stringBuilder = new StringBuilder()
                .append("Exception thrown")
                .append("\n\nError message:\n")
                .append(exception.getMessage());

        Throwable cause = exception.getCause();
        if (cause != null) {
            stringBuilder
                    .append("\n\nError cause:\n")
                    .append(cause);
        }

        if (exception instanceof GatewayException) {
            GatewayException gatewayException = (GatewayException) exception;

            String responseCode = gatewayException.getResponseCode();
            if (isNotNullOrBlank(responseCode)) {
                stringBuilder
                        .append("\n\nResponse code:\n")
                        .append(responseCode);
            }

            String responseText = gatewayException.getResponseText();
            if (isNotNullOrBlank(responseText)) {
                stringBuilder
                        .append("\n\nResponse text:\n")
                        .append(responseText);
            }
        }

        return stringBuilder.toString();
    }
}
