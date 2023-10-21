package aionem.net.sdk.core.utils;

import aionem.net.sdk.core.data.AlnDBCol;
import aionem.net.sdk.core.data.AlnData;
import com.google.gson.JsonObject;
import lombok.extern.log4j.Log4j2;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.time.Clock;
import java.util.*;


@Log4j2
public class AlnUtilsData {


    public static <T> T adaptTo(final Class<T> type, final JsonObject data) throws Exception {
        return adaptTo(type, (Object) data);
    }
    public static <T> T adaptTo(final Class<T> type, final Object data) throws Exception {
        T t;
        if(type.getSuperclass().isAssignableFrom(AlnData.class) || type.isAssignableFrom(AlnData.class)) {
            return type.getConstructor(data.getClass()).newInstance(data);
        }else {
            t = type.getConstructor().newInstance();

            if(type.getSuperclass().isAssignableFrom(HashMap.class) || type.isAssignableFrom(HashMap.class)) {
                return adaptTo(t, AlnUtilsJson.fromHashMap((HashMap<String, Object>) data));
            }else if(type.getSuperclass().isAssignableFrom(JsonObject.class) || type.isAssignableFrom(JsonObject.class)) {
                return adaptTo(t, (JsonObject) data);
            }else {
                return t;
            }
        }
    }

    public static <T> T adaptTo(final T t, final JsonObject data) throws Exception {
        if(data == null) return null;
        for(final Field field : t.getClass().getDeclaredFields()) {
            final int modifiers = field.getModifiers();
            final boolean isStatic = Modifier.isStatic(modifiers);
            final boolean isFinal = Modifier.isFinal(modifiers);
            final boolean isPrivate = Modifier.isPrivate(modifiers);
            if(!isStatic && !isFinal && !isPrivate) {
                field.setAccessible(true);
                final AlnDBCol col = field.isAnnotationPresent(AlnDBCol.class) ? field.getDeclaredAnnotation(AlnDBCol.class) : null;
                final String fieldName = field.getName();
                final String name = col != null ? AlnUtilsText.notEmpty(col.name(), fieldName) : fieldName;
                Object value = AlnUtilsJson.getValue(data, name);
                if(value != null) {
                    value = convert(value, field.getType());
                }
                field.set(t, value);
            }
        }
        return t;
    }

    public static <T> T convert(final Object object, final T defaultValue) {
        if(defaultValue == null) return null;
        T value = (T) convert(object, defaultValue.getClass());
        return value != null ? value : defaultValue;
    }
    public static <T> T convert(final Object object, final Class<T> type) {
        if(object == null || type == null) return null;
        if(type.isAssignableFrom(object.getClass())) {
            return (T) object;
        }else if(type.isArray()) {
            return (T) convertToArray(object, type.getComponentType());
        }else if(Calendar.class.isAssignableFrom(type) && object instanceof Date) {
            return (T) Converter.DateUtils.toCalendar((Date)object);
        }else if(type == Date.class && object instanceof Calendar) {
            return (T) Converter.DateUtils.toDate((Calendar)object);
        }else {
            String valueString = AlnUtilsText.toString(object);
            if(valueString == null) {
                return null;
            }else if(type == String.class) {
                return (T) valueString;
            }else if(type == Boolean.class) {
                if("true".equalsIgnoreCase(valueString) || "1".equals(valueString)) {
                    return (T) Boolean.TRUE;
                }else {
                    return "false".equalsIgnoreCase(valueString) || "0".equals(valueString) ? (T) Boolean.FALSE : null;
                }
            }else {
                try {
                    Object value;
                    if(type == Byte.class) {
                        value = Byte.parseByte(valueString);
                    }else if(type == Short.class) {
                        value = Short.parseShort(valueString);
                    }else if(type == Integer.class) {
                        value = Integer.parseInt(valueString);
                    }else if(type == Long.class) {
                        value = Long.parseLong(valueString);
                    }else if(type == Float.class) {
                        value = Float.parseFloat(valueString);
                    }else if(type == Double.class) {
                        value = Double.parseDouble(valueString);
                    }else if(type == BigDecimal.class) {
                        value = new BigDecimal(valueString);
                    }else if(Calendar.class.isAssignableFrom(type)) {
                        value = Converter.DateUtils.calendarFromString(valueString);
                    }else if(Date.class.isAssignableFrom(type)) {
                        value = Converter.DateUtils.dateFromString(valueString);
                    }else {
                        value = null;
                    }
                    return (T) value;
                }catch(Exception ignore) {
                    return null;
                }
            }
        }
    }

    private static <T> T[] convertToArray(final Object obj, final Class<T> type) {
        if(obj.getClass().isArray()) {
            List<Object> resultList = new ArrayList<>();
            for(int i = 0; i < Array.getLength(obj); ++i) {
                T singleValueResult = convert(Array.get(obj, i), type);
                if(singleValueResult != null) {
                    resultList.add(singleValueResult);
                }
            }
            return (T[]) resultList.toArray((Object[])Array.newInstance(type, resultList.size()));
        }else {
            T singleValueResult = convert(obj, type);
            if(singleValueResult == null) {
                return (T[]) Array.newInstance(type, 0);
            }else {
                T[] arrayResult = (T[]) Array.newInstance(type, 1);
                arrayResult[0] = singleValueResult;
                return arrayResult;
            }
        }
    }

    public static final class Converter {

        public static class DateUtils {

            public static Calendar toCalendar(Date input) {
                if(input == null) {
                    return null;
                }else {
                    Calendar result = Calendar.getInstance();
                    result.setTime(input);
                    return result;
                }
            }

            public static Date toDate(final Calendar input) {
                return input == null ? null : input.getTime();
            }

            public static String dateToString(final Date input) {
                return calendarToString(toCalendar(input));
            }

            public static String calendarToString(final Calendar input) {
                return input == null ? null : format(input);
            }

            public static Date dateFromString(final String input) {
                return toDate(calendarFromString(input));
            }

            public static Calendar calendarFromString(String input) {
                return input == null ? null : parse(input);
            }


            public static Calendar parse(final String text) {
                if(text == null) {
                    throw new IllegalArgumentException("argument can not be null");
                }else {
                    byte sign;
                    int start;
                    if(text.startsWith("-")) {
                        sign = 45;
                        start = 1;
                    }else if(text.startsWith("+")) {
                        sign = 43;
                        start = 1;
                    }else {
                        sign = 43;
                        start = 0;
                    }

                    int year;
                    int month;
                    int day;
                    int hour;
                    int min;
                    int sec;
                    int ms;
                    TimeZone tz;
                    try {
                        year = Integer.parseInt(text.substring(start, start + 4));
                        start += 4;
                        if(text.charAt(start) != '-') {
                            return null;
                        }

                        ++start;
                        month = Integer.parseInt(text.substring(start, start + 2));
                        start += 2;
                        if(text.charAt(start) != '-') {
                            return null;
                        }

                        ++start;
                        day = Integer.parseInt(text.substring(start, start + 2));
                        start += 2;
                        if(text.charAt(start) != 'T') {
                            return null;
                        }

                        ++start;
                        hour = Integer.parseInt(text.substring(start, start + 2));
                        start += 2;
                        if(text.charAt(start) != ':') {
                            return null;
                        }

                        ++start;
                        min = Integer.parseInt(text.substring(start, start + 2));
                        start += 2;
                        if(text.charAt(start) != ':') {
                            return null;
                        }

                        ++start;
                        sec = Integer.parseInt(text.substring(start, start + 2));
                        start += 2;
                        if(text.charAt(start) != '.') {
                            return null;
                        }

                        ++start;
                        ms = Integer.parseInt(text.substring(start, start + 3));
                        start += 3;
                        String tzid = text.substring(start);
                        tz = TZS.get(tzid);
                        if(tz == null) {
                            tzid = "GMT" + tzid;
                            tz = TimeZone.getTimeZone(tzid);
                            if(!tz.getID().equals(tzid)) {
                                return null;
                            }
                        }
                    }catch(IndexOutOfBoundsException | NumberFormatException var14) {
                        return null;
                    }

                    final Calendar cal = Calendar.getInstance(tz);
                    cal.setLenient(false);
                    if(sign != 45 && year != 0) {
                        cal.set(Calendar.YEAR, year);
                        cal.set(Calendar.ERA, 1);
                    }else {
                        cal.set(Calendar.YEAR, year + 1);
                        cal.set(Calendar.ERA, 0);
                    }

                    cal.set(Calendar.MONTH, month - 1);
                    cal.set(Calendar.DATE, day);
                    cal.set(Calendar.HOUR_OF_DAY, hour);
                    cal.set(Calendar.MINUTE, min);
                    cal.set(Calendar.SECOND, sec);
                    cal.set(Calendar.MILLISECOND, ms);

                    try {
                        cal.getTime();
                        getYear(cal);
                        return cal;
                    }catch(IllegalArgumentException var13) {
                        return null;
                    }
                }
            }

            public static String format(final Date date) throws IllegalArgumentException {
                return format(date, 0);
            }

            public static String format(final Clock clock) throws IllegalArgumentException {
                return format(clock.millis(), clock.getZone().getRules().getOffset(clock.instant()).getTotalSeconds());
            }

            public static String format(final long millisSinceEpoch) throws IllegalArgumentException {
                return format(millisSinceEpoch, 0);
            }

            public static String format(final Date date, int tzOffsetInSeconds) throws IllegalArgumentException {
                if(date == null) {
                    throw new IllegalArgumentException("argument can not be null");
                }else {
                    return format(date.getTime(), tzOffsetInSeconds);
                }
            }

            public static String format(long millisSinceEpoch, int tzOffsetInSeconds) throws IllegalArgumentException {
                final Calendar cal = Calendar.getInstance();
                cal.setTimeZone(tzOffsetInSeconds == 0 ? UTC : new SimpleTimeZone(tzOffsetInSeconds * 1000, ""));
                cal.setTimeInMillis(millisSinceEpoch);
                return format(cal);
            }

            public static String format(final Calendar cal) throws IllegalArgumentException {
                return format(cal, true);
            }

            private static String format(final Calendar cal, boolean includeMs) throws IllegalArgumentException {
                if(cal == null) {
                    throw new IllegalArgumentException("argument can not be null");
                }else {
                    StringBuilder buf = new StringBuilder();
                    appendZeroPaddedInt(buf, getYear(cal), 4);
                    buf.append('-');
                    appendZeroPaddedInt(buf, cal.get(Calendar.MONTH) + 1, 2);
                    buf.append('-');
                    appendZeroPaddedInt(buf, cal.get(Calendar.DATE), 2);
                    buf.append('T');
                    appendZeroPaddedInt(buf, cal.get(Calendar.HOUR_OF_DAY), 2);
                    buf.append(':');
                    appendZeroPaddedInt(buf, cal.get(Calendar.MINUTE), 2);
                    buf.append(':');
                    appendZeroPaddedInt(buf, cal.get(Calendar.SECOND), 2);
                    if(includeMs) {
                        buf.append('.');
                        appendZeroPaddedInt(buf, cal.get(Calendar.MILLISECOND), 3);
                    }

                    TimeZone tz = cal.getTimeZone();
                    int offset = tz.getOffset(cal.getTimeInMillis());
                    if(offset != 0) {
                        int hours = Math.abs(offset / '\uea60' / 60);
                        int minutes = Math.abs(offset / '\uea60' % 60);
                        buf.append(offset < 0 ? '-' : '+');
                        appendZeroPaddedInt(buf, hours, 2);
                        buf.append(':');
                        appendZeroPaddedInt(buf, minutes, 2);
                    }else {
                        buf.append('Z');
                    }

                    return buf.toString();
                }
            }

            public static int getYear(final Calendar calendar) throws IllegalArgumentException {
                int year = calendar.get(Calendar.YEAR);
                if(calendar.isSet(Calendar.ERA) && calendar.get(Calendar.ERA) == 0) {
                    year = 1 - year;
                }

                if(year <= 9999 && year >= -9999) {
                    return year;
                }else {
                    throw new IllegalArgumentException("Calendar has more than four year digits, cannot be formatted as ISO8601: " + year);
                }
            }

            private static void appendZeroPaddedInt(final StringBuilder buf, int n, final int precision) {
                if(n < 0) {
                    buf.append('-');
                    n = -n;
                }

                for(int exp = precision - 1; exp > 0 && (double) n < Math.pow(10.0, exp); --exp) {
                    buf.append('0');
                }

                buf.append(n);
            }

            private static final TimeZone UTC;
            private static final Map<String, TimeZone> TZS = new HashMap<>();
            static {
                final TimeZone gmt = TimeZone.getTimeZone("GMT");
                TZS.put("Z", gmt);
                TZS.put("+00:00", gmt);
                TZS.put("-00:00", gmt);
                final String[] tzs = new String[]{"-12:00", "-11:00", "-10:00", "-09:30", "-09:00", "-08:00", "-07:00", "-06:00", "-05:00", "-04:30", "-04:00", "-03:30", "-03:00", "-02:00", "-01:00", "+01:00", "+02:00", "+03:00", "+03:30", "+04:00", "+04:30", "+05:00", "+05:30", "+05:45", "+06:00", "+06:30", "+07:00", "+08:00", "+08:45", "+09:00", "+09:30", "+10:00", "+10:30", "+11:00", "+11:30", "+12:00", "+12:45", "+13:00", "+14:00"};

                for(final String tz : tzs) {
                    TZS.put(tz, TimeZone.getTimeZone("GMT" + tz));
                }

                UTC = TimeZone.getTimeZone("UTC");
            }

        }

    }

}
