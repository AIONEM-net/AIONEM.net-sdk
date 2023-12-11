package aionem.net.sdk.core.utils;

import lombok.extern.log4j.Log4j2;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


@Log4j2
public class UtilsParse {


    public static boolean isNumber(final Object value) {
        boolean isNumber;
        try {
            final String valueString = UtilsText.toString(value);
            Double.parseDouble(valueString);
            isNumber = true;
        }catch(Exception ignore) {
            isNumber = false;
        }
        return isNumber;
    }

    public static double toNumber(final Object value, final double defaultValue) {
        if(value == null) return defaultValue;
        double number = defaultValue;
        final String valueString = UtilsText.toString(value, Double.toString(defaultValue));
        if(!UtilsText.isEmpty(valueString)) {
            try {
                number = Double.parseDouble(valueString);
            }catch(Exception ignore) {
            }
        }
        return number;
    }

    public static boolean isBoolean(final Object value) {
        boolean isBoolean;
        try {
            final String valueString = UtilsText.toString(value);
            isBoolean = valueString.equalsIgnoreCase("true") || valueString.equalsIgnoreCase("false");
        }catch(Exception ignore) {
            isBoolean = false;
        }
        return isBoolean;
    }

    public static boolean toBoolean(final Object value, final boolean defaultValue) {
        if(value == null) return defaultValue;
        boolean bool = defaultValue;
        final String valueString = UtilsText.toString(value, Boolean.toString(defaultValue));
        if(!UtilsText.isEmpty(valueString)) {
            try {
                bool = Boolean.parseBoolean(valueString);
            }catch(Exception ignore) {
            }
        }
        return bool;
    }

    public static Date parseDate(final String date) {
        return parseDate(date, "");
    }

    public static Date parseDate(final String date, final String format) {
        return parseDate(date, format, Locale.getDefault());
    }

    public static Date parseDate(final String date, String format, Locale locale) {
        if(UtilsText.isEmpty(format)) format = "dd/MM/yyyy";
        if(locale == null) locale = Locale.getDefault();
        try {
            final DateFormat simpleDateFormat = new SimpleDateFormat(format, locale);
            return simpleDateFormat.parse(date);
        }catch(Exception e) {
            log.error("\nAIONEM.NET-SDK: ERROR WHILE PARSING DATE "+ e);
            return null;
        }
    }

    public static String formatMoney(final double amount) {
        final DecimalFormat decimalFormat = new DecimalFormat("#,##0.0");
        return decimalFormat.format(amount);
    }

}
