package aionem.net.sdk.web.filters;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.MessageFormat;
import java.util.*;
import java.util.regex.Pattern;
import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

import lombok.Getter;
import lombok.extern.log4j.Log4j2;


@Log4j2
public class ExpiresFilter implements Filter {

    private static final Pattern commaSeparatedValuesPattern = Pattern.compile("\\s*,\\s*");
    private static final String HEADER_CACHE_CONTROL = "Cache-Control";
    private static final String HEADER_EXPIRES = "Expires";
    private static final String HEADER_LAST_MODIFIED = "Last-Modified";
    private static final String PARAMETER_EXPIRES_BY_TYPE = "ExpiresByType";
    private static final String PARAMETER_EXPIRES_DEFAULT = "ExpiresDefault";
    private static final String PARAMETER_EXPIRES_EXCLUDED_RESPONSE_STATUS_CODES = "ExpiresExcludedResponseStatusCodes";

    protected static final StringManager sm = StringManager.getManager("org.apache.catalina.filters");
    private ExpiresConfiguration defaultExpiresConfiguration;
    private int[] excludedResponseStatusCodes = new int[]{304};
    private final Map<String, ExpiresConfiguration> expiresConfigurationByContentType = new LinkedHashMap<>();

    protected static int[] commaDelimitedListToIntArray(String commaDelimitedInts) {
        String[] intsAsStrings = commaDelimitedListToStringArray(commaDelimitedInts);
        int[] ints = new int[intsAsStrings.length];

        for(int i = 0; i < intsAsStrings.length; ++i) {
            String intAsString = intsAsStrings[i];

            try {
                ints[i] = Integer.parseInt(intAsString);
            } catch (NumberFormatException var6) {
                throw new RuntimeException("Exception parsing number '" + i + "' (zero based) of comma delimited list '" + commaDelimitedInts + "'");
            }
        }

        return ints;
    }

    protected static String[] commaDelimitedListToStringArray(String commaDelimitedStrings) {
        return commaDelimitedStrings != null && !commaDelimitedStrings.isEmpty() ? commaSeparatedValuesPattern.split(commaDelimitedStrings) : new String[0];
    }

    protected static boolean contains(String str, String searchStr) {
        if (str != null && searchStr != null) {
            return str.contains(searchStr);
        } else {
            return false;
        }
    }

    protected static String intsToCommaDelimitedString(int[] ints) {
        if (ints == null) {
            return "";
        } else {
            StringBuilder result = new StringBuilder();

            for(int i = 0; i < ints.length; ++i) {
                result.append(ints[i]);
                if (i < ints.length - 1) {
                    result.append(", ");
                }
            }

            return result.toString();
        }
    }

    protected static boolean isEmpty(String str) {
        return str == null || str.isEmpty();
    }

    protected static boolean isNotEmpty(String str) {
        return !isEmpty(str);
    }

    protected static boolean startsWithIgnoreCase(String string, String prefix) {
        if (string != null && prefix != null) {
            return prefix.length() <= string.length() && string.regionMatches(true, 0, prefix, 0, prefix.length());
        } else {
            return string == null && prefix == null;
        }
    }

    protected static String substringBefore(String str, String separator) {
        if (str != null && !str.isEmpty() && separator != null) {
            if (separator.isEmpty()) {
                return "";
            } else {
                int separatorIndex = str.indexOf(separator);
                return separatorIndex == -1 ? str : str.substring(0, separatorIndex);
            }
        } else {
            return null;
        }
    }

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        if (request instanceof HttpServletRequest && response instanceof HttpServletResponse) {
            HttpServletRequest httpRequest = (HttpServletRequest)request;
            HttpServletResponse httpResponse = (HttpServletResponse)response;
            if (response.isCommitted()) {
                if (log.isDebugEnabled()) {
                    log.debug(sm.getString("expiresFilter.responseAlreadyCommited", new Object[]{httpRequest.getRequestURL()}));
                }

                chain.doFilter(request, response);
            } else {
                XHttpServletResponse xResponse = new XHttpServletResponse(httpRequest, httpResponse);
                chain.doFilter(request, xResponse);
                if (!xResponse.isWriteResponseBodyStarted()) {
                    this.onBeforeWriteResponseBody(httpRequest, xResponse);
                }
            }
        } else {
            chain.doFilter(request, response);
        }

    }

    protected Date getExpirationDate(XHttpServletResponse response) {
        String contentType = response.getContentType();
        ExpiresConfiguration configuration = this.expiresConfigurationByContentType.get(contentType);
        Date result;
        if (configuration != null) {
            result = this.getExpirationDate(configuration, response);
            if (log.isDebugEnabled()) {
                log.debug(sm.getString("expiresFilter.useMatchingConfiguration", new Object[]{configuration, contentType, contentType, result}));
            }

            return result;
        } else {
            String majorType;
            if (contains(contentType, ";")) {
                majorType = substringBefore(contentType, ";").trim();
                configuration = this.expiresConfigurationByContentType.get(majorType);
                if (configuration != null) {
                    result = this.getExpirationDate(configuration, response);
                    if (log.isDebugEnabled()) {
                        log.debug(sm.getString("expiresFilter.useMatchingConfiguration", new Object[]{configuration, majorType, contentType, result}));
                    }

                    return result;
                }
            }

            if (contains(contentType, "/")) {
                majorType = substringBefore(contentType, "/");
                configuration = this.expiresConfigurationByContentType.get(majorType);
                if (configuration != null) {
                    result = this.getExpirationDate(configuration, response);
                    if (log.isDebugEnabled()) {
                        log.debug(sm.getString("expiresFilter.useMatchingConfiguration", new Object[]{configuration, majorType, contentType, result}));
                    }

                    return result;
                }
            }

            if (this.defaultExpiresConfiguration != null) {
                result = this.getExpirationDate(this.defaultExpiresConfiguration, response);
                if (log.isDebugEnabled()) {
                    log.debug(sm.getString("expiresFilter.useDefaultConfiguration", new Object[]{this.defaultExpiresConfiguration, contentType, result}));
                }

                return result;
            } else {
                if (log.isDebugEnabled()) {
                    log.debug(sm.getString("expiresFilter.noExpirationConfiguredForContentType", new Object[]{contentType}));
                }

                return null;
            }
        }
    }

    protected Date getExpirationDate(ExpiresConfiguration configuration, XHttpServletResponse response) {
        Calendar calendar;
        switch (configuration.getStartingPoint()) {
            case ACCESS_TIME:
                calendar = Calendar.getInstance();
                break;
            case LAST_MODIFICATION_TIME:
                if (response.isLastModifiedHeaderSet()) {
                    try {
                        long lastModified = response.getLastModifiedHeader();
                        calendar = Calendar.getInstance();
                        calendar.setTimeInMillis(lastModified);
                    } catch (NumberFormatException var6) {
                        calendar = Calendar.getInstance();
                    }
                } else {
                    calendar = Calendar.getInstance();
                }
                break;
            default:
                throw new IllegalStateException(sm.getString("expiresFilter.unsupportedStartingPoint", new Object[]{configuration.getStartingPoint()}));
        }

        for(Duration duration : configuration.getDurations()) {
            calendar.add(duration.getUnit().getCalendarField(), duration.getAmount());
        }

        return calendar.getTime();
    }

    public void init(FilterConfig filterConfig) throws ServletException {
        Enumeration<String> names = filterConfig.getInitParameterNames();

        while(names.hasMoreElements()) {
            String name = names.nextElement();
            String value = filterConfig.getInitParameter(name);

            try {
                if (name.startsWith("ExpiresByType")) {
                    String contentType = name.substring("ExpiresByType".length()).trim();
                    ExpiresConfiguration expiresConfiguration = this.parseExpiresConfiguration(value);
                    this.expiresConfigurationByContentType.put(contentType, expiresConfiguration);
                } else if (name.equalsIgnoreCase("ExpiresDefault")) {
                    this.defaultExpiresConfiguration = this.parseExpiresConfiguration(value);
                } else if (name.equalsIgnoreCase("ExpiresExcludedResponseStatusCodes")) {
                    this.excludedResponseStatusCodes = commaDelimitedListToIntArray(value);
                } else {
                    log.warn(sm.getString("expiresFilter.unknownParameterIgnored", new Object[]{name, value}));
                }
            } catch (RuntimeException var7) {
                throw new ServletException(sm.getString("expiresFilter.exceptionProcessingParameter", new Object[]{name, value}), var7);
            }
        }

        log.debug(sm.getString("expiresFilter.filterInitialized", new Object[]{this.toString()}));
    }

    protected boolean isEligibleToExpirationHeaderGeneration(HttpServletRequest request, XHttpServletResponse response) {
        boolean expirationHeaderHasBeenSet = response.containsHeader("Expires") || contains(response.getCacheControlHeader(), "max-age");
        if (expirationHeaderHasBeenSet) {
            if (log.isDebugEnabled()) {
                log.debug(sm.getString("expiresFilter.expirationHeaderAlreadyDefined", request.getRequestURI(), response.getStatus(), response.getContentType()));
            }

            return false;
        } else {
            for(int skippedStatusCode : this.excludedResponseStatusCodes) {
                if (response.getStatus() == skippedStatusCode) {
                    if (log.isDebugEnabled()) {
                        log.debug(sm.getString("expiresFilter.skippedStatusCode", request.getRequestURI(), response.getStatus(), response.getContentType()));
                    }

                    return false;
                }
            }

            return true;
        }
    }

    public void onBeforeWriteResponseBody(HttpServletRequest request, XHttpServletResponse response) {
        if (this.isEligibleToExpirationHeaderGeneration(request, response)) {
            Date expirationDate = this.getExpirationDate(response);
            if (expirationDate == null) {
                if (log.isDebugEnabled()) {
                    log.debug(sm.getString("expiresFilter.noExpirationConfigured", request.getRequestURI(), response.getStatus(), response.getContentType()));
                }
            } else {
                if (log.isDebugEnabled()) {
                    log.debug(sm.getString("expiresFilter.setExpirationDate", request.getRequestURI(), response.getStatus(), response.getContentType(), expirationDate));
                }

                String maxAgeDirective = "max-age=" + (expirationDate.getTime() - System.currentTimeMillis()) / 1000L;
                String cacheControlHeader = response.getCacheControlHeader();
                String newCacheControlHeader = cacheControlHeader == null ? maxAgeDirective : cacheControlHeader + ", " + maxAgeDirective;
                response.setHeader("Cache-Control", newCacheControlHeader);
                response.setDateHeader("Expires", expirationDate.getTime());
            }

        }
    }

    protected ExpiresConfiguration parseExpiresConfiguration(String inputLine) {
        String line = inputLine.trim();
        StringTokenizer tokenizer = new StringTokenizer(line, " ");

        String currentToken;
        try {
            currentToken = tokenizer.nextToken();
        } catch (NoSuchElementException var14) {
            throw new IllegalStateException(sm.getString("expiresFilter.startingPointNotFound", new Object[]{line}));
        }

        StartingPoint startingPoint;
        if (!"access".equalsIgnoreCase(currentToken) && !"now".equalsIgnoreCase(currentToken)) {
            if ("modification".equalsIgnoreCase(currentToken)) {
                startingPoint = ExpiresFilter.StartingPoint.LAST_MODIFICATION_TIME;
            } else if (!tokenizer.hasMoreTokens() && startsWithIgnoreCase(currentToken, "a")) {
                startingPoint = ExpiresFilter.StartingPoint.ACCESS_TIME;
                tokenizer = new StringTokenizer(currentToken.substring(1) + " seconds", " ");
            } else {
                if (tokenizer.hasMoreTokens() || !startsWithIgnoreCase(currentToken, "m")) {
                    throw new IllegalStateException(sm.getString("expiresFilter.startingPointInvalid", new Object[]{currentToken, line}));
                }

                startingPoint = ExpiresFilter.StartingPoint.LAST_MODIFICATION_TIME;
                tokenizer = new StringTokenizer(currentToken.substring(1) + " seconds", " ");
            }
        } else {
            startingPoint = ExpiresFilter.StartingPoint.ACCESS_TIME;
        }

        try {
            currentToken = tokenizer.nextToken();
        } catch (NoSuchElementException var13) {
            throw new IllegalStateException(sm.getString("Duration not found in directive '{}'", new Object[]{line}));
        }

        if ("plus".equalsIgnoreCase(currentToken)) {
            try {
                currentToken = tokenizer.nextToken();
            } catch (NoSuchElementException var12) {
                throw new IllegalStateException(sm.getString("Duration not found in directive '{}'", new Object[]{line}));
            }
        }

        List<Duration> durations = new ArrayList<>();

        while(currentToken != null) {
            int amount;
            try {
                amount = Integer.parseInt(currentToken);
            } catch (NumberFormatException var11) {
                throw new IllegalStateException(sm.getString("Invalid duration (number) '{}' in directive '{}'", new Object[]{currentToken, line}));
            }

            try {
                currentToken = tokenizer.nextToken();
            } catch (NoSuchElementException var10) {
                throw new IllegalStateException(sm.getString("Duration unit not found after amount {} in directive '{}'", new Object[]{amount, line}));
            }

            DurationUnit durationUnit;
            if ("years".equalsIgnoreCase(currentToken)) {
                durationUnit = ExpiresFilter.DurationUnit.YEAR;
            } else if (!"month".equalsIgnoreCase(currentToken) && !"months".equalsIgnoreCase(currentToken)) {
                if (!"week".equalsIgnoreCase(currentToken) && !"weeks".equalsIgnoreCase(currentToken)) {
                    if (!"day".equalsIgnoreCase(currentToken) && !"days".equalsIgnoreCase(currentToken)) {
                        if (!"hour".equalsIgnoreCase(currentToken) && !"hours".equalsIgnoreCase(currentToken)) {
                            if (!"minute".equalsIgnoreCase(currentToken) && !"minutes".equalsIgnoreCase(currentToken)) {
                                if (!"second".equalsIgnoreCase(currentToken) && !"seconds".equalsIgnoreCase(currentToken)) {
                                    throw new IllegalStateException(sm.getString("Invalid duration unit (years|months|weeks|days|hours|minutes|seconds) '{}' in directive '{}'", new Object[]{currentToken, line}));
                                }

                                durationUnit = ExpiresFilter.DurationUnit.SECOND;
                            } else {
                                durationUnit = ExpiresFilter.DurationUnit.MINUTE;
                            }
                        } else {
                            durationUnit = ExpiresFilter.DurationUnit.HOUR;
                        }
                    } else {
                        durationUnit = ExpiresFilter.DurationUnit.DAY;
                    }
                } else {
                    durationUnit = ExpiresFilter.DurationUnit.WEEK;
                }
            } else {
                durationUnit = ExpiresFilter.DurationUnit.MONTH;
            }

            Duration duration = new Duration(amount, durationUnit);
            durations.add(duration);
            if (tokenizer.hasMoreTokens()) {
                currentToken = tokenizer.nextToken();
            } else {
                currentToken = null;
            }
        }

        return new ExpiresConfiguration(startingPoint, durations);
    }

    @Override
    public void destroy() {

    }

    public String toString() {
        return this.getClass().getSimpleName() + "[excludedResponseStatusCode=[" + intsToCommaDelimitedString(this.excludedResponseStatusCodes) + "], default=" + this.defaultExpiresConfiguration + ", byType=" + this.expiresConfigurationByContentType + "]";
    }

    @Getter
    protected static class Duration {
        protected final int amount;
        protected final DurationUnit unit;

        public Duration(int amount, DurationUnit unit) {
            this.amount = amount;
            this.unit = unit;
        }

        public String toString() {
            return this.amount + " " + this.unit;
        }
    }

    @Getter
    protected static enum DurationUnit {
        DAY(6),
        HOUR(10),
        MINUTE(12),
        MONTH(2),
        SECOND(13),
        WEEK(3),
        YEAR(1);

        private final int calendarField;

        DurationUnit(int calendarField) {
            this.calendarField = calendarField;
        }
    }

    @Getter
    protected static class ExpiresConfiguration {
        private final List<Duration> durations;
        private final StartingPoint startingPoint;

        public ExpiresConfiguration(StartingPoint startingPoint, List<Duration> durations) {
            this.startingPoint = startingPoint;
            this.durations = durations;
        }

        public String toString() {
            return "ExpiresConfiguration[startingPoint=" + this.startingPoint + ", duration=" + this.durations + "]";
        }
    }

    protected enum StartingPoint {
        ACCESS_TIME,
        LAST_MODIFICATION_TIME;

        StartingPoint() {
        }
    }

    public class XHttpServletResponse extends HttpServletResponseWrapper {
        private String cacheControlHeader;
        private long lastModifiedHeader;
        private boolean lastModifiedHeaderSet;
        private PrintWriter printWriter;
        private final HttpServletRequest request;
        private ServletOutputStream servletOutputStream;
        private boolean writeResponseBodyStarted;

        public XHttpServletResponse(HttpServletRequest request, HttpServletResponse response) {
            super(response);
            this.request = request;
        }

        public void addDateHeader(String name, long date) {
            super.addDateHeader(name, date);
            if (!this.lastModifiedHeaderSet) {
                this.lastModifiedHeader = date;
                this.lastModifiedHeaderSet = true;
            }

        }

        public void addHeader(String name, String value) {
            super.addHeader(name, value);
            if ("Cache-Control".equalsIgnoreCase(name) && this.cacheControlHeader == null) {
                this.cacheControlHeader = value;
            }

        }

        public String getCacheControlHeader() {
            return this.cacheControlHeader;
        }

        public long getLastModifiedHeader() {
            return this.lastModifiedHeader;
        }

        public ServletOutputStream getOutputStream() throws IOException {
            if (this.servletOutputStream == null) {
                this.servletOutputStream = ExpiresFilter.this.new XServletOutputStream(super.getOutputStream(), this.request, this);
            }

            return this.servletOutputStream;
        }

        public PrintWriter getWriter() throws IOException {
            if (this.printWriter == null) {
                this.printWriter = ExpiresFilter.this.new XPrintWriter(super.getWriter(), this.request, this);
            }

            return this.printWriter;
        }

        public boolean isLastModifiedHeaderSet() {
            return this.lastModifiedHeaderSet;
        }

        public boolean isWriteResponseBodyStarted() {
            return this.writeResponseBodyStarted;
        }

        public void reset() {
            super.reset();
            this.lastModifiedHeader = 0L;
            this.lastModifiedHeaderSet = false;
            this.cacheControlHeader = null;
        }

        public void setDateHeader(String name, long date) {
            super.setDateHeader(name, date);
            if ("Last-Modified".equalsIgnoreCase(name)) {
                this.lastModifiedHeader = date;
                this.lastModifiedHeaderSet = true;
            }

        }

        public void setHeader(String name, String value) {
            super.setHeader(name, value);
            if ("Cache-Control".equalsIgnoreCase(name)) {
                this.cacheControlHeader = value;
            }

        }

        public void setWriteResponseBodyStarted(boolean writeResponseBodyStarted) {
            this.writeResponseBodyStarted = writeResponseBodyStarted;
        }
    }

    public class XPrintWriter extends PrintWriter {
        private final PrintWriter out;
        private final HttpServletRequest request;
        private final XHttpServletResponse response;

        public XPrintWriter(PrintWriter out, HttpServletRequest request, XHttpServletResponse response) {
            super(out);
            this.out = out;
            this.request = request;
            this.response = response;
        }

        public PrintWriter append(char c) {
            this.fireBeforeWriteResponseBodyEvent();
            return this.out.append(c);
        }

        public PrintWriter append(CharSequence csq) {
            this.fireBeforeWriteResponseBodyEvent();
            return this.out.append(csq);
        }

        public PrintWriter append(CharSequence csq, int start, int end) {
            this.fireBeforeWriteResponseBodyEvent();
            return this.out.append(csq, start, end);
        }

        public void close() {
            this.fireBeforeWriteResponseBodyEvent();
            this.out.close();
        }

        private void fireBeforeWriteResponseBodyEvent() {
            if (!this.response.isWriteResponseBodyStarted()) {
                this.response.setWriteResponseBodyStarted(true);
                ExpiresFilter.this.onBeforeWriteResponseBody(this.request, this.response);
            }

        }

        public void flush() {
            this.fireBeforeWriteResponseBodyEvent();
            this.out.flush();
        }

        public void print(boolean b) {
            this.fireBeforeWriteResponseBodyEvent();
            this.out.print(b);
        }

        public void print(char c) {
            this.fireBeforeWriteResponseBodyEvent();
            this.out.print(c);
        }

        public void print(char[] s) {
            this.fireBeforeWriteResponseBodyEvent();
            this.out.print(s);
        }

        public void print(double d) {
            this.fireBeforeWriteResponseBodyEvent();
            this.out.print(d);
        }

        public void print(float f) {
            this.fireBeforeWriteResponseBodyEvent();
            this.out.print(f);
        }

        public void print(int i) {
            this.fireBeforeWriteResponseBodyEvent();
            this.out.print(i);
        }

        public void print(long l) {
            this.fireBeforeWriteResponseBodyEvent();
            this.out.print(l);
        }

        public void print(Object obj) {
            this.fireBeforeWriteResponseBodyEvent();
            this.out.print(obj);
        }

        public void print(String s) {
            this.fireBeforeWriteResponseBodyEvent();
            this.out.print(s);
        }

        public PrintWriter printf(Locale l, String format, Object... args) {
            this.fireBeforeWriteResponseBodyEvent();
            return this.out.printf(l, format, args);
        }

        public PrintWriter printf(String format, Object... args) {
            this.fireBeforeWriteResponseBodyEvent();
            return this.out.printf(format, args);
        }

        public void println() {
            this.fireBeforeWriteResponseBodyEvent();
            this.out.println();
        }

        public void println(boolean x) {
            this.fireBeforeWriteResponseBodyEvent();
            this.out.println(x);
        }

        public void println(char x) {
            this.fireBeforeWriteResponseBodyEvent();
            this.out.println(x);
        }

        public void println(char[] x) {
            this.fireBeforeWriteResponseBodyEvent();
            this.out.println(x);
        }

        public void println(double x) {
            this.fireBeforeWriteResponseBodyEvent();
            this.out.println(x);
        }

        public void println(float x) {
            this.fireBeforeWriteResponseBodyEvent();
            this.out.println(x);
        }

        public void println(int x) {
            this.fireBeforeWriteResponseBodyEvent();
            this.out.println(x);
        }

        public void println(long x) {
            this.fireBeforeWriteResponseBodyEvent();
            this.out.println(x);
        }

        public void println(Object x) {
            this.fireBeforeWriteResponseBodyEvent();
            this.out.println(x);
        }

        public void println(String x) {
            this.fireBeforeWriteResponseBodyEvent();
            this.out.println(x);
        }

        public void write(char[] buf) {
            this.fireBeforeWriteResponseBodyEvent();
            this.out.write(buf);
        }

        public void write(char[] buf, int off, int len) {
            this.fireBeforeWriteResponseBodyEvent();
            this.out.write(buf, off, len);
        }

        public void write(int c) {
            this.fireBeforeWriteResponseBodyEvent();
            this.out.write(c);
        }

        public void write(String s) {
            this.fireBeforeWriteResponseBodyEvent();
            this.out.write(s);
        }

        public void write(String s, int off, int len) {
            this.fireBeforeWriteResponseBodyEvent();
            this.out.write(s, off, len);
        }
    }

    public class XServletOutputStream extends ServletOutputStream {
        private final HttpServletRequest request;
        private final XHttpServletResponse response;
        private final ServletOutputStream servletOutputStream;

        public XServletOutputStream(ServletOutputStream servletOutputStream, HttpServletRequest request, XHttpServletResponse response) {
            this.servletOutputStream = servletOutputStream;
            this.response = response;
            this.request = request;
        }

        public void close() throws IOException {
            this.fireOnBeforeWriteResponseBodyEvent();
            this.servletOutputStream.close();
        }

        private void fireOnBeforeWriteResponseBodyEvent() {
            if (!this.response.isWriteResponseBodyStarted()) {
                this.response.setWriteResponseBodyStarted(true);
                ExpiresFilter.this.onBeforeWriteResponseBody(this.request, this.response);
            }

        }

        public void flush() throws IOException {
            this.fireOnBeforeWriteResponseBodyEvent();
            this.servletOutputStream.flush();
        }

        public void print(boolean b) throws IOException {
            this.fireOnBeforeWriteResponseBodyEvent();
            this.servletOutputStream.print(b);
        }

        public void print(char c) throws IOException {
            this.fireOnBeforeWriteResponseBodyEvent();
            this.servletOutputStream.print(c);
        }

        public void print(double d) throws IOException {
            this.fireOnBeforeWriteResponseBodyEvent();
            this.servletOutputStream.print(d);
        }

        public void print(float f) throws IOException {
            this.fireOnBeforeWriteResponseBodyEvent();
            this.servletOutputStream.print(f);
        }

        public void print(int i) throws IOException {
            this.fireOnBeforeWriteResponseBodyEvent();
            this.servletOutputStream.print(i);
        }

        public void print(long l) throws IOException {
            this.fireOnBeforeWriteResponseBodyEvent();
            this.servletOutputStream.print(l);
        }

        public void print(String s) throws IOException {
            this.fireOnBeforeWriteResponseBodyEvent();
            this.servletOutputStream.print(s);
        }

        public void println() throws IOException {
            this.fireOnBeforeWriteResponseBodyEvent();
            this.servletOutputStream.println();
        }

        public void println(boolean b) throws IOException {
            this.fireOnBeforeWriteResponseBodyEvent();
            this.servletOutputStream.println(b);
        }

        public void println(char c) throws IOException {
            this.fireOnBeforeWriteResponseBodyEvent();
            this.servletOutputStream.println(c);
        }

        public void println(double d) throws IOException {
            this.fireOnBeforeWriteResponseBodyEvent();
            this.servletOutputStream.println(d);
        }

        @Override
        public boolean isReady() {
            return false;
        }

        @Override
        public void setWriteListener(WriteListener writeListener) {

        }

        public void println(float f) throws IOException {
            this.fireOnBeforeWriteResponseBodyEvent();
            this.servletOutputStream.println(f);
        }

        public void println(int i) throws IOException {
            this.fireOnBeforeWriteResponseBodyEvent();
            this.servletOutputStream.println(i);
        }

        public void println(long l) throws IOException {
            this.fireOnBeforeWriteResponseBodyEvent();
            this.servletOutputStream.println(l);
        }

        public void println(String s) throws IOException {
            this.fireOnBeforeWriteResponseBodyEvent();
            this.servletOutputStream.println(s);
        }

        public void write(byte[] b) throws IOException {
            this.fireOnBeforeWriteResponseBodyEvent();
            this.servletOutputStream.write(b);
        }

        public void write(byte[] b, int off, int len) throws IOException {
            this.fireOnBeforeWriteResponseBodyEvent();
            this.servletOutputStream.write(b, off, len);
        }

        public void write(int b) throws IOException {
            this.fireOnBeforeWriteResponseBodyEvent();
            this.servletOutputStream.write(b);
        }
    }

    public static class StringManager {

        private static int LOCALE_CACHE_SIZE = 10;

        private final ResourceBundle bundle;
        private final Locale locale;

        private StringManager(String packageName, Locale locale) {
            String bundleName = packageName + ".LocalStrings";
            ResourceBundle bnd = null;
            try {
                bnd = ResourceBundle.getBundle(bundleName, locale);
            } catch (MissingResourceException ex) {
                ClassLoader cl = Thread.currentThread().getContextClassLoader();
                if (cl != null) {
                    try {
                        bnd = ResourceBundle.getBundle(bundleName, locale, cl);
                    } catch (MissingResourceException ignored) {
                    }
                }
            }
            bundle = bnd;
            if (bundle != null) {
                Locale bundleLocale = bundle.getLocale();
                if (bundleLocale.equals(Locale.ROOT)) {
                    this.locale = Locale.ENGLISH;
                } else {
                    this.locale = bundleLocale;
                }
            } else {
                this.locale = null;
            }
        }

        public String getString(String key) {
            if (key == null){
                String msg = "key may not have a null value";
                throw new IllegalArgumentException(msg);
            }

            String str = null;

            try {
                if (bundle != null) {
                    str = bundle.getString(key);
                }
            } catch (MissingResourceException mre) {
                str = null;
            }

            return str;
        }


        public String getString(final String key, final Object... args) {
            String value = getString(key);
            if (value == null) {
                value = key;
            }

            MessageFormat mf = new MessageFormat(value);
            mf.setLocale(locale);
            return mf.format(args, new StringBuffer(), null).toString();
        }

        public Locale getLocale() {
            return locale;
        }

        private static final Map<String, Map> managers = new Hashtable<>();


        public static StringManager getManager(String packageName) {
            return getManager(packageName, Locale.getDefault());
        }

        public static synchronized StringManager getManager(
                String packageName, Locale locale) {

            Map<Locale, StringManager> map = managers.get(packageName);
            if (map == null) {
                map = new LinkedHashMap(LOCALE_CACHE_SIZE, 1, true) {
                    private static final long serialVersionUID = 1L;
                    @Override
                    protected boolean removeEldestEntry(
                            Map.Entry eldest) {
                        if (size() > (LOCALE_CACHE_SIZE - 1)) {
                            return true;
                        }
                        return false;
                    }
                };
                managers.put(packageName, map);
            }

            StringManager mgr = map.get(locale);
            if (mgr == null) {
                mgr = new StringManager(packageName, locale);
                map.put(locale, mgr);
            }
            return mgr;
        }

    }

}
