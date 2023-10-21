package aionem.net.sdk.core.utils;

import lombok.extern.log4j.Log4j2;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


@Log4j2
public class AlnUtilsDB {

    public static final String PAR_ID = "id";
    public static final String PAR_DRAW = "draw";
    public static final String PAR_START = "start";
    public static final String PAR_MAX = "max";
    public static final String PAR_LENGTH = "length";
    public static final String PAR_PAGE = "page";
    public static final String PAR_SEARCH = "search";
    public static final String PAR_SEARCH_VALUE = "search[value]";
    public static String DB_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";


    public static String formatDate(final String format) {
        return formatDate(Calendar.getInstance(), format);
    }

    public static String formatDate(final long milliSec, final String format) {
        final Calendar calendar = Calendar.getInstance();
        final long time = Calendar.getInstance().getTimeInMillis() + milliSec;
        calendar.setTimeInMillis(time);
        return formatDate(calendar, format);
    }

    public static String formatDate(final Calendar calendar, final String format) {
        return formatDate(calendar, format, Locale.getDefault());
    }

    public static String formatDate(final Calendar calendar, final String format, final Locale locale) {
        return formatDate(calendar.getTime(), format, locale);
    }

    public static final String DATE_FORMAT = "dd/MM/yyyy";
    public static String formatDate(final Date date, String format, Locale locale) {
        String formatDate = "";
        try {
            if(date != null) {
                if(AlnUtilsText.isEmpty(format)) format = DATE_FORMAT;
                if(locale == null) locale = Locale.getDefault();

                final DateFormat simpleDateFormat = new SimpleDateFormat(format, locale);
                formatDate = simpleDateFormat.format(date.getTime());
            }
        }catch(Exception e) {
            log.error("\nAIONEM.NET-SDK: ERROR WHILE PARSING DATE " + e +"\n");
        }
        return formatDate;
    }

    public static String formatDateDB() {
        return formatDate(Calendar.getInstance(), DB_DATE_FORMAT);
    }

    public static String formatDateDB(long milliSec) {
        return formatDate(milliSec, DB_DATE_FORMAT);
    }

    public static String formatDateDB(final Calendar calendar) {
        return formatDate(calendar, DB_DATE_FORMAT);
    }

    public static String formatDateDB(final Calendar calendar, final Locale locale) {
        return formatDate(calendar, DB_DATE_FORMAT, locale);
    }

    public static String formatDateDB(final Date date, final Locale locale) {
        return formatDate(date, DB_DATE_FORMAT, locale);
    }

}
