package aionem.net.sdk.web;

import aionem.net.sdk.core.utils.UtilsConverter;
import aionem.net.sdk.core.utils.UtilsNetwork;
import aionem.net.sdk.core.utils.UtilsText;
import aionem.net.sdk.data.dao.I18n;
import aionem.net.sdk.data.utils.UtilsResource;
import aionem.net.sdk.web.dao.PageManager;
import aionem.net.sdk.web.config.ConfEnv;
import aionem.net.sdk.web.beans.Page;
import aionem.net.sdk.web.beans.Properties;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.jsp.JspContext;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Locale;


@Log4j2
public @Getter class WebContext {

    protected HttpServletRequest request;
    protected HttpServletResponse response;
    protected PageContext pageContext;
    protected ServletContext servletContext;
    protected HttpSession session;

    public WebContext(final JspContext jspContext) {
        init(jspContext);
    }

    public WebContext(final PageContext pageContext) {
        init(pageContext);
    }

    public WebContext(final ServletRequest request, final ServletResponse response) {
        init(request, response);
    }

    public WebContext(final HttpServletRequest request, final HttpServletResponse response) {
        init(request, response);
    }

    public WebContext init(final JspContext jspContext) {
        return init((PageContext) jspContext);
    }

    public WebContext init(final PageContext pageContext) {
        return init((HttpServletRequest) pageContext.getRequest(), (HttpServletResponse) pageContext.getResponse(), pageContext);
    }

    public WebContext init(final ServletRequest request, final ServletResponse response) {
        return init((HttpServletRequest) request, (HttpServletResponse) response);
    }

    public WebContext init(final HttpServletRequest request, final HttpServletResponse response) {
        return init(request, response, null);
    }

    public WebContext(final HttpServletRequest request, final HttpServletResponse response, final PageContext pageContext) {
        init(request, response, pageContext);
    }

    public WebContext init(final HttpServletRequest request, final HttpServletResponse response, final PageContext pageContext) {
        this.request = request;
        this.response = response;
        this.pageContext = pageContext;
        this.servletContext = request.getServletContext();
        this.session = request.getSession(true);
        return this;
    }

    public WebContext setup() {
        setRequestAttribute("webContext", this);
        getPageProperties();
        getI18n();
        getCurrentPage();
        getHomePage();
        return this;
    }

    public ConfEnv getConfEnv() {
        return ConfEnv.getInstance();
    }

    public PageManager getPageManager() {
        return PageManager.getInstance();
    }

    public Object getRequestAttribute(final String name) {
        return request != null ? request.getAttribute(name) : null;
    }

    public <T> T getRequestAttribute(final String name, final Object defaultValue) {
        return getRequestAttribute(name, defaultValue, false);
    }

    public <T> T getRequestAttribute(final String name, final Object defaultValue, final boolean create) {
        final Object value = getRequestAttribute(name);
        if(value == null && create) setRequestAttribute(name, defaultValue);
        return (T) UtilsConverter.convert(value, defaultValue);
    }

    public <T> T getRequestAttribute(final String name, final Class<T> type) {
        return UtilsConverter.convert(getRequestAttribute(name), type);
    }

    public void setRequestAttribute(final String name, final Object value) {
        if(request != null) request.setAttribute(name, value);
    }

    public void removeRequestAttribute(final String name) {
        if(request != null) request.removeAttribute(name);
    }

    public Object getPageAttribute(final String name) {
        return pageContext != null ? pageContext.getAttribute(name, PageContext.PAGE_SCOPE) : null;
    }

    public <T> T getPageAttribute(final String name, final Object defaultValue) {
        return getPageAttribute(name, defaultValue, false);
    }

    public <T> T getPageAttribute(final String name, final Object defaultValue, final boolean create) {
        final Object value = getPageAttribute(name);
        if(value == null) setPageAttribute(name, defaultValue);
        return (T) UtilsConverter.convert(value, defaultValue);
    }

    public <T> T getPageAttribute(final String name, final Class<T> type) {
        return UtilsConverter.convert(getPageAttribute(name), type);
    }

    public void setPageAttribute(final String name, final Object value) {
        if(pageContext != null) pageContext.setAttribute(name, value, PageContext.PAGE_SCOPE);
    }

    public void removePageAttribute(final String name) {
        if(pageContext != null) pageContext.removeAttribute(name, PageContext.PAGE_SCOPE);
    }

    public Object getApplicationAttribute(final String name) {
        return getServletContext() != null ? getServletContext().getAttribute(name) : null;
    }

    public <T> T getApplicationAttribute(final String name, final Object defaultValue) {
        return getApplicationAttribute(name, defaultValue, false);
    }

    public <T> T getApplicationAttribute(final String name, final Object defaultValue, final boolean create) {
        final Object value = getApplicationAttribute(name);
        if(value == null && create) setApplicationAttribute(name, defaultValue);
        return (T) UtilsConverter.convert(value, defaultValue);
    }

    public <T> T getApplicationAttribute(final String name, final Class<T> type) {
        return UtilsConverter.convert(getApplicationAttribute(name), type);
    }

    public void setApplicationAttribute(final String name, final Object value) {
        if(getServletContext() != null) getServletContext().setAttribute(name, value);
    }

    public void removeApplicationAttribute(final String name) {
        if(getServletContext() != null) getServletContext().getAttribute(name);
    }

    public Object getSessionAttribute(final String name) {
        return session != null ? session.getAttribute(name) : null;
    }

    public <T> T getSessionAttribute(final String name, final Object defaultValue) {
        return getSessionAttribute(name, defaultValue, false);
    }

    public <T> T getSessionAttribute(final String name, final Object defaultValue, final boolean create) {
        final Object value = getSessionAttribute(name);
        if(value == null && create) setSessionAttribute(name, defaultValue);
        return (T) UtilsConverter.convert(value, defaultValue);
    }

    public <T> T getSessionAttribute(final String name, final Class<T> type) {
        return UtilsConverter.convert(getSessionAttribute(name), type);
    }

    public void setSessionAttribute(final String name, final Object value) {
        if(session != null) session.setAttribute(name, value);
    }

    public void removeSessionAttribute(final String name) {
        if(session != null) session.removeAttribute(name);
    }

    public String getParameter(final String name) {
        return request.getParameter(name);
    }

    public String getInitParameter(final String name) {
        return getInitParameter(name, "");
    }

    public String getInitParameter(final String name, final String defaultValue) {
        return servletContext != null ? UtilsText.notNull(servletContext.getInitParameter(name), defaultValue) : defaultValue;
    }

    public String getRealPathPageCurrent(final String path) {
        return UtilsResource.getRealPathRoot("/ui.page"+ getServletPage() + (!UtilsText.isEmpty(path) ? "/" + path : ""));
    }

    public String getServletPath() {
        return request.getServletPath().replace("/index.jsp", "");
    }

    public String getServletPage() {
        final boolean needHome = !isSite() || isUnderHome() || isRoot();
        return (needHome ? getConfEnv().getHome() : "") + request.getServletPath().replace("/index.jsp", "");
    }

    public boolean isRoot() {
        final String requestRoot = getRequestRoot();
        return UtilsText.isEmpty(requestRoot) || requestRoot.equals("/");
    }

    public boolean isHome() {
        return isRoot() || getRequestURI().equalsIgnoreCase(ConfEnv.getInstance().getHome());
    }

    public boolean isSite() {
        return getConfEnv().getSites().contains(getRequestRoot());
    }

    public boolean isUnderHome() {
        return getRequestRoot().startsWith(ConfEnv.getInstance().getHome());
    }

    public String getContextPath() {
        return ("");
    }

    public String getContextServletPath() {
        return getServletPath();
    }

    public String getContextServletPage() {
        return getServletPage();
    }

    public String getRequestURI() {
        return request.getRequestURI();
    }

    public String getRequestUrl() {
        return getConfEnv().getUrl(getRequestURI());
    }

    public String getRequestRoot() {
        String[] paths = getRequestURI().split("/");
        return "/" + (paths.length > 0 ? paths[1] : "");
    }

    public String getRequestQuery() {
        return getRequestQuery(false);
    }

    public String getRequestQuery(final boolean questionMark) {
        final String queryString = request.getQueryString();
        return UtilsText.notEmptyUse(queryString, (questionMark ? "?" : "") + queryString);
    }

    public String getRequestUrlQuery() {
        final String query = getRequestQuery();
        return getRequestURI() + UtilsText.notEmptyUse(query, "?"+ query);
    }

    public String getUrlQuery() {
        return getRequestUrlQuery();
    }

    public I18n getI18n() {
        final I18n i18n = getRequestAttribute("i18n", I18n.class);
        return i18n != null ? i18n : getRequestAttribute("i18n", new I18n(getLocale()), true);
    }

    public Page getHomePage() {
        final Page homePage = getRequestAttribute("homePage", Page.class);
        return homePage != null ? homePage : getRequestAttribute("homePage", getPageManager().getHomePage(getCurrentPage()), true);
    }

    public Page getCurrentPage() {
        final Page currentPage = getRequestAttribute("currentPage", Page.class);
        return currentPage != null ? currentPage : getRequestAttribute("currentPage", new Page(this), true);
    }

    public Properties getPageProperties() {
        final Properties properties = getRequestAttribute("pageProperties", Properties.class);
        return properties != null ? properties : getRequestAttribute("pageProperties", new Properties(this), true);
    }

    public String getProtocol() {
        return request.getProtocol();
    }

    public String getRemoteHost() {
        return request.getRemoteHost();
    }

    public String getRemoteAddr() {
        return request.getRemoteAddr();
    }

    public int getRemotePort() {
        return request.getRemotePort();
    }

    public int getLocalPort() {
        return request.getLocalPort();
    }

    public int getServerPort() {
        return request.getServerPort();
    }

    public String getHeader(final String name) {
        return request.getHeader(name);
    }

    public void setHeader(final String name, final String value) {
        response.setHeader(name, value);
    }

    public String getLanguage() {

        String language = getRequestAttribute("language", String.class);

        if(UtilsText.isEmpty(language)) {

            language = getParameter("language");

            if(UtilsText.isEmpty(language)) {
                language = getSessionAttribute("language", "");
            }
            if(UtilsText.isEmpty(language)) {
                language = getPageManager().getLanguage(getCurrentPage());
            }
            if(UtilsText.isEmpty(language)) {
                language = getHeader("Accept-Language");
                if(!UtilsText.isEmpty(language) && language.contains("-")) {
                    language = language.substring(0, language.indexOf("-"));
                }
            }

            language = UtilsText.notEmpty(language, "en");
            setRequestAttribute("language", language);
        }

        return language;
    }

    public Locale getLocale() {
        final Locale locale = getRequestAttribute("locale", Locale.class);
        return locale != null ? locale : getRequestAttribute("locale", new Locale(getLanguage()), true);
    }

    public boolean isPublishMode() {
        return !isEditMode();
    }

    public boolean isEditMode() {
        return "true".equalsIgnoreCase(getParameter("ui.edit"));
    }

    public String getRedirect(final String location) {
        return getRedirect(location, false);
    }

    public String getRedirect(final String location, boolean isRedirect) {
        String url = location;
        if(isRedirect) {
            url = UtilsNetwork.addParameter(url, "redirect", getUrlQuery());
        }
        return url;
    }

    public void sendRedirect(final String location) throws IOException {
        response.sendRedirect(location);
    }

    public void sendError(final int error) throws IOException {
        response.sendError(error);
    }

    public void forward(final String path) throws IOException, ServletException {
        if(pageContext != null) {
            pageContext.forward(path);
        }else {
            getRequestDispatcher(path).forward(request, response);
        }
    }

    public void include(final String path) throws IOException, ServletException {
        if(pageContext != null) {
            pageContext.include(path);
        }else {
            getRequestDispatcher(path).include(request, response);
        }
    }

    public void includeCatch(final String path) {
        try {
            include(path);
        }catch (Exception e) {
            log.error("INCLUDE: " + path, e);
        }
    }

    public RequestDispatcher getRequestDispatcher(final String path) {
        return request.getRequestDispatcher(path);
    }

    public JspWriter getOut() throws IOException {
        return pageContext.getOut();
    }

    public PrintWriter getWriter() throws IOException {
        return response.getWriter();
    }

    public PrintWriter print(final String line) throws IOException {
        final PrintWriter writer = getWriter();
        writer.print(line);
        return writer;
    }

    public PrintWriter println(final String line) throws IOException {
        final PrintWriter writer = getWriter();
        writer.println(line);
        return writer;
    }

    public boolean isHostMatch() {
        return getConfEnv().getHost().equalsIgnoreCase(getRemoteHost());
    }

    public boolean isRemoteLocal() {
        final String remoteHost = request.getRemoteHost();
        return "0:0:0:0:0:0:0:1".equalsIgnoreCase(remoteHost) || "127.0.0.1".equalsIgnoreCase(remoteHost) || "localhost".equalsIgnoreCase(remoteHost);
    }

}
