package aionem.net.sdk.web;

import aionem.net.sdk.core.utils.UtilsConverter;
import aionem.net.sdk.core.Env;
import aionem.net.sdk.data.Data;
import aionem.net.sdk.core.utils.UtilsNetwork;
import aionem.net.sdk.core.utils.UtilsText;
import aionem.net.sdk.web.modals.*;
import aionem.net.sdk.web.utils.UtilsWeb;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import javax.servlet.jsp.PageContext;
import java.io.*;
import java.util.Locale;


@Log4j2
public @Getter class AioWeb {

    protected HttpServletRequest request;
    protected HttpServletResponse response;
    protected PageContext pageContext;
    protected HttpSession session;
    protected String contextPath;

    private PageManager pageManager;

    public AioWeb() {

    }

    public AioWeb(final ServletRequest request, final ServletResponse response) {
        init((HttpServletRequest) request, (HttpServletResponse) response);
    }

    public AioWeb(final HttpServletRequest request, final HttpServletResponse response) {
        init(request, response);
    }

    public AioWeb init(final HttpServletRequest request, final HttpServletResponse response) {
        return init(request, response, null);
    }

    public AioWeb init(final HttpServletRequest request, final HttpServletResponse response, final PageContext pageContext) {
        this.request = request;
        this.response = response;
        this.pageContext = pageContext;
        this.session = request.getSession(true);
        this.contextPath = getConfEnv().getContextPath();
        return this;
    }

    public ConfEnv getConfEnv() {
        return ConfEnv.getInstance(this);
    }

    public PageManager getPageManager() {
        if(pageManager == null) {
            pageManager = new PageManager(this);
        }
        return pageManager;
    }

    public boolean isPublishMode() {
        return true;
    }

    public void setAttribute(final Object value) {
        setAttribute(Properties.PROPERTIES, value);
    }

    public void setAttribute(final Data data) {
        setAttribute(name(data.getClass()), data.toString());
    }

    public void setAttribute(final String name, Object value) {
        request.setAttribute(name, value);
    }

    public <T> T getAttribute(final String name, final Object defaultValue) {
        return (T) UtilsConverter.convert(getPageAttribute(name), defaultValue);
    }

    public <T> T getAttribute(final String name, final Class<T> type) {
        return UtilsConverter.convert(getPageAttribute(name), type);
    }

    public Object getAttribute(final String name) {
        return getRequest().getAttribute(name);
    }

    public <T> T getPageAttribute(final String name, final Object defaultValue) {
        return (T) UtilsConverter.convert(getPageAttribute(name), defaultValue);
    }

    public <T> T getPageAttribute(final String name, final Class<T> type) {
        return UtilsConverter.convert(getPageAttribute(name), type);
    }

    public Object getPageAttribute(final String name) {
        return getPageContext().getAttribute(name, PageContext.PAGE_SCOPE);
    }

    public <T> T getApplicationAttribute(final String name, final Object defaultValue) {
        return (T) UtilsConverter.convert(getApplicationAttribute(name), defaultValue);
    }

    public <T> T getApplicationAttribute(final String name, final Class<T> type) {
        return UtilsConverter.convert(getApplicationAttribute(name), type);
    }

    public Object getApplicationAttribute(final String name) {
        return getPageContext().getAttribute(name, PageContext.APPLICATION_SCOPE);
    }

    public <T> T getSessionAttribute(final String name, final Object defaultValue) {
        return (T) UtilsConverter.convert(getSessionAttribute(name), defaultValue);
    }

    public <T> T getSessionAttribute(final String name, final Class<T> type) {
        return UtilsConverter.convert(getSessionAttribute(name), type);
    }

    public Object getSessionAttribute(final String name) {
        return getSession().getAttribute(name);
    }

    public static String name(final Component component) {
        return name(component.getClass());
    }

    public static String name(final Class<?> type) {
        return type.getPackageName() +"."+ type.getName();
    }

    public String value(final Component component) {
        return value(component.getProperties(), component.getClass());
    }

    public String value(final Component component, final Class<?> type) {
        return value(component.getProperties(), type);
    }

    public String value(final Component component, final String name) {
        return value(component.getProperties(), name);
    }

    public String value(final Properties properties, final Class<?> type) {
        return value(properties.getData(), type);
    }

    public String value(final Properties properties, final String name) {
        return value(properties.getData(), name);
    }

    public String value(final Data data, final Class<?> type) {
        return value(data, name(type));
    }

    public String value(final Object data, final String name) {
        if(!Properties.PROPERTIES.equals(name)) {
            getRequest().setAttribute(name, data);
        }
        getRequest().setAttribute(Properties.PROPERTIES, data);
        return data.toString();
    }

    public ServletContext getServletContext() {
        return getRequest().getServletContext();
    }

    public String getParameter(final String name) {
        return getRequest().getParameter(name);
    }

    public String getInitParameter(final String name) {
        return getServletContext().getInitParameter(name);
    }

    public String getRealPathCurrent() {
        return getRealPathCurrent("");
    }

    public String getRealPathCurrent(final String path) {
        return getRealPathRoot(getServletPath()) + (!UtilsText.isEmpty(path) ? "/" + path : "");
    }

    public String getRealPathWebInf() {
        return getRealPathWebInf("");
    }

    public String getRealPathWebInf(final String path) {
        return getRealPathRoot("/WEB-INF"+ (!UtilsText.isEmpty(path) ? "/" + path : ""));
    }

    public String getRealPathPage() {
        return getRealPathWebInf("");
    }

    public String getRealPathPage(final String path) {
        return getRealPathRoot("/ui.page"+ (!UtilsText.isEmpty(path) ? "/" + path : ""));
    }

    public String getRealPathRoot() {
        return getRealPathRoot("");
    }

    public String getRealPathRoot(final String path) {
        String realPath = getServletContext().getRealPath(path);
        realPath = realPath.replace("//", "/");
        if(realPath.endsWith("/")) realPath = realPath.substring(0, realPath.length()-1);
        return realPath;
    }

    public String getServletPath() {
        return getRequest().getServletPath().replace("/index.jsp", "");
    }

    public String getContextPath() {
        return UtilsText.notNull(contextPath, getRequest().getContextPath());
    }

    public String getContextPath(final String path) {
        String contextPath = getContextPath() +"/"+ path;
        contextPath = contextPath.replace("//", "/");
        if(contextPath.endsWith("/")) contextPath = contextPath.substring(0, contextPath.length()-1);
        return contextPath;
    }
    public String getContextServletPath() {
        return getContextPath() + getServletPath();
    }

    public String getRelativePath(String path) {
        final String realPathRoot = getRealPathRoot();
        if(!path.startsWith("/")) path = "/" + path;
        if(path.startsWith(realPathRoot)) {
            path = path.substring(realPathRoot.length());
        }
        return path;
    }

    public String getRequestURI() {
        return getRequest().getRequestURI();
    }

    public String getDomain() {
        final String domain = isLocal() ? "127.0.0.1" : getConfEnv().getDomain();
        return (getRequest().isSecure() ? "https" : "http") +"://"+ domain +":"+ getServerPort();
    }

    public String getURI() {
        return getURI(getRequestURI());
    }

    public String getURI(final String uri) {
        return getDomain() + uri;
    }

    public String getRequestUrl() {
        final String contextPath = getContextPath();
        final String requestURI = getRequestURI();
        return requestURI.substring(contextPath.length());
    }

    public String getRequestRoot() {
        String[] paths = getRequestUrl().split("/");
        return "/" + (paths.length > 0 ? paths[1] : "");
    }

    public String getRequestQuery() {
        return UtilsText.notNull(getRequest().getQueryString());
    }

    public String getRequestUrlQuery() {
        final String query = getRequestQuery();
        return getRequestUrl() + UtilsText.notEmptyUse(query, "?"+ query);
    }

    public String getContextUrlQuery() {
        return getContextPath(getRequestUrlQuery());
    }

    public String getProtocol() {
        return getRequest().getProtocol();
    }

    public String getRemoteHost() {
        return getRequest().getRemoteHost();
    }

    public String getRemoteAddr() {
        return getRequest().getRemoteAddr();
    }

    public int getRemotePort() {
        return getRequest().getRemotePort();
    }

    public int getLocalPort() {
        return getRequest().getLocalPort();
    }

    public int getServerPort() {
        return getRequest().getServerPort();
    }

    public ClassLoader getClassLoader() {
        return getClass().getClassLoader();
    }

    public InputStream getResourceAsStream(final String name) {
        return getClassLoader().getResourceAsStream(name);
    }

    public PrintWriter getWriter() throws IOException {
        return getResponse().getWriter();
    }

    public String readFile(final String fileName) {
        return UtilsWeb.readFileResource(this, fileName);
    }

    public String getHeader(final String name) {
        return getRequest().getHeader(name);
    }

    public String getLanguage() {
        return UtilsText.notEmpty(getSessionAttribute("language"), "en");
    }

    public Locale getLocale() {
        return new Locale(getLanguage());
    }

    public boolean isDisabledMode() {
        return !isEditMode();
    }

    public boolean isEditMode() {
        return "true".equalsIgnoreCase(getParameter("ui.edit"));
    }

    public String getRedirect(final String location) {
        return getRedirect(location, false);
    }

    public String getRedirect(final String location, boolean isRedirect) {
        String url = location.startsWith("http") ? location : getContextPath(location);
        if(isRedirect) {
            url = UtilsNetwork.addParameter(url, "redirect", getContextUrlQuery());
        }
        return url;
    }

    public RequestDispatcher getRequestDispatcher(final String path) {
        return getRequest().getRequestDispatcher(path);
    }

    public void forward(final String path) throws IOException, ServletException {
        getRequestDispatcher(path).forward(getRequest(), getResponse());
    }

    public void include(final String path) throws IOException, ServletException {
        getRequestDispatcher(path).include(getRequest(), getResponse());
    }

    public boolean isHostMatch() {
        return getConfEnv().getHost().equalsIgnoreCase(getRemoteHost());
    }

    public boolean isLocal() {
        final String remoteHost = getRequest().getRemoteHost();
        return "0:0:0:0:0:0:0:1".equalsIgnoreCase(remoteHost) || "127.0.0.1".equalsIgnoreCase(remoteHost) || "localhost".equalsIgnoreCase(remoteHost);
    }

}
