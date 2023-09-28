package aionem.net.sdk.jsp;

import aionem.net.sdk.data.AlnData;
import aionem.net.sdk.utils.AlnTextUtils;
import lombok.Getter;
import lombok.extern.log4j.Log4j;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Locale;


@Log4j
public @Getter class AlnJsp {

    public static final String PROPERTIES = "properties";

    protected HttpServletRequest request;
    protected HttpServletResponse response;
    protected HttpSession session;
    public String propertiesKey;

    public AlnJsp() {
        this.propertiesKey = PROPERTIES;
    }
    public AlnJsp(final HttpServletRequest request, final HttpServletResponse response) {
        init(request, response);
    }
    public AlnJsp(final HttpServletRequest request, final HttpServletResponse response, final Class<?> type) {
        init(request, response, type);
    }

    public AlnJsp init(final HttpServletRequest request, final HttpServletResponse response) {
        return init(request, response, PROPERTIES);
    }
    public AlnJsp init(final HttpServletRequest request, final HttpServletResponse response, final Class<?> type) {
        return init(request, response, name(type));
    }
    public AlnJsp init(final HttpServletRequest request, final HttpServletResponse response, final String propertiesKey) {
        this.request = request;
        this.response = response;
        this.session = request.getSession(true);
        this.propertiesKey = propertiesKey;
        return this;
    }

    public void setAttribute(final Object value) {
        setAttribute(PROPERTIES, value);
    }
    public void setAttribute(final AlnData data) {
        setAttribute(name(data.getClass()), data.toString());
    }
    public void setAttribute(final String name, Object value) {
        request.setAttribute(name, value);
    }

    public Object getAttribute(final String name) {
        return request.getAttribute(name);
    }
    public <T> T getAttribute(final Object object) {
        return (T) getAttribute(object.getClass());
    }
    public <T> T getAttribute(final Class<T> type) {
        return (T) request.getAttribute(name(type));
    }

    public String name(final AlnCmp alnCmp) {
        return name(alnCmp.getClass());
    }
    public String name(final Class<?> type) {
        return type.getPackageName() +"."+ type.getName();
    }
    public String value(final AlnCmp alnCmp) {
        return value(alnCmp.getProperties(), alnCmp.getClass());
    }
    public String value(final AlnCmp alnCmp, final Class<?> type) {
        return value(alnCmp.getProperties(), type);
    }
    public String value(final AlnCmp alnCmp, final String name) {
        return value(alnCmp.getProperties(), name);
    }
    public String value(final AlnJspProperties properties, final Class<?> type) {
        return value(properties.getData(), type);
    }
    public String value(final AlnJspProperties properties, final String name) {
        return value(properties.getData(), name);
    }
    public String value(final AlnData data, final Class<?> type) {
        return value(data, name(type));
    }
    public String value(final Object data, final String name) {
        request.setAttribute(name, data);
        return data.toString();
    }

    public ServletContext getServletContext() {
        return request.getServletContext();
    }

    public String getServletPath() {
        return request.getServletPath().replace("/index.jsp", "");
    }
    public String getContextPath() {
        return request.getContextPath();
    }
    public String getContextPath(final String path) {
        String contextPath = getContextPath() +"/"+ path;
        contextPath = contextPath.replace("//", "/");
        return contextPath;
    }
    public String getContextServletPath() {
        return getContextPath() + getServletPath();
    }
    public String getRequestURI() {
        return request.getRequestURI();
    }
    public String getRequestUrl() {
        String contextPath = getContextPath();
        String requestURI = getRequestURI();
        return requestURI.substring(contextPath.length());
    }

    public void noCache() {
        response.setHeader("Dispatcher", "no-cache");
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Cache-Control", "no-cache");
    }

    public String getLanguage() {
        return AlnTextUtils.notEmpty(session.getAttribute("language"), "en");
    }

    public Locale getLocale() {
        return new Locale(getLanguage());
    }

}
