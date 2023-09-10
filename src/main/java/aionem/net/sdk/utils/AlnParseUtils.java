package aionem.net.sdk.utils;

import lombok.extern.log4j.Log4j;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


@Log4j
public class AlnParseUtils {


    public static boolean isNumber(final Object value) {
        boolean isNumber;
        try {
            final String valueString = AlnTextUtils.toString(value);
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
        final String valueString = AlnTextUtils.toString(value, String.valueOf(defaultValue));
        if(!AlnTextUtils.isEmpty(valueString)) {
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
            final String valueString = AlnTextUtils.toString(value);
            isBoolean = valueString.equalsIgnoreCase("true") || valueString.equalsIgnoreCase("false");
        }catch(Exception ignore) {
            isBoolean = false;
        }
        return isBoolean;
    }

    public static boolean toBoolean(final Object value, final boolean defaultValue) {
        if(value == null) return defaultValue;
        boolean bool = defaultValue;
        final String valueString = AlnTextUtils.toString(value, String.valueOf(defaultValue));
        if(!AlnTextUtils.isEmpty(valueString)) {
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
        if(AlnTextUtils.isEmpty(format)) format = "dd/MM/yyyy";
        if(locale == null) locale = Locale.getDefault();
        try {
            final DateFormat simpleDateFormat = new SimpleDateFormat(format, locale);
            return simpleDateFormat.parse(date);
        }catch(Exception e) {
            log.error("\nAIONEM.NET-AEM: ERROR WHILE PARSING DATE "+ e);
            return null;
        }
    }

    public static String formatMoney(final double amount) {
        final DecimalFormat decimalFormat = new DecimalFormat("#,##0.0");
        return decimalFormat.format(amount);
    }

}
