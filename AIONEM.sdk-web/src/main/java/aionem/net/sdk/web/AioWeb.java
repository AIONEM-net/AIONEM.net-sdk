package aionem.net.sdk.web;

import aionem.net.sdk.core.utils.UtilsConverter;
import aionem.net.sdk.core.utils.UtilsNetwork;
import aionem.net.sdk.core.utils.UtilsText;
import aionem.net.sdk.data.utils.UtilsResource;
import aionem.net.sdk.web.modals.ConfEnv;
import aionem.net.sdk.web.dao.PageManager;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.jsp.PageContext;
import java.io.IOException;
import java.util.Locale;


@Log4j2
public @Getter class AioWeb {

    protected HttpServletRequest request;
    protected HttpServletResponse response;
    protected PageContext pageContext;
    protected ServletContext servletContext;
    protected HttpSession session;

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
        this.servletContext = request.getServletContext();
        this.session = request.getSession(true);
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

    public void removeRequestAttribute(final String name) {
        if(request != null) request.removeAttribute(name);
    }

    public Object getPageAttribute(final String name) {
        return pageContext != null ? pageContext.getAttribute(name, PageContext.PAGE_SCOPE) : null;
    }

    public void removePageAttribute(final String name) {
        if(pageContext != null) pageContext.removeAttribute(name, PageContext.PAGE_SCOPE);
    }

    public Object getApplicationAttribute(final String name) {
        return pageContext != null ? pageContext.getAttribute(name, PageContext.APPLICATION_SCOPE) : null;
    }

    public void removeApplicationAttribute(final String name) {
        if(pageContext != null) pageContext.getAttribute(name, PageContext.APPLICATION_SCOPE);
    }

    public <T> T getSessionAttribute(final String name, final Object defaultValue) {
        return (T) UtilsConverter.convert(getSessionAttribute(name), defaultValue);
    }

    public <T> T getSessionAttribute(final String name, final Class<T> type) {
        return UtilsConverter.convert(getSessionAttribute(name), type);
    }

    public Object getSessionAttribute(final String name) {
        return session != null ? session.getAttribute(name) : null;
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

    public String getRealPathCurrent() {
        return getRealPathCurrent("");
    }

    public String getRealPathCurrent(final String path) {
        return UtilsResource.getRealPathRoot(getServletPath()) + (!UtilsText.isEmpty(path) ? "/" + path : "");
    }

    public String getRealPathPageCurrent(final String path) {
        return UtilsResource.getRealPathRoot("/ui.page"+ getServletPage() + (!UtilsText.isEmpty(path) ? "/" + path : ""));
    }

    public String getServletPath() {
        return request.getServletPath().replace("/index.jsp", "");
    }

    public String getServletPage() {
        final String requestRoot = getRequestRoot();
        final String sites = getSites();
        final boolean needHome = isUnderHome() || !sites.contains(requestRoot);
        return (needHome ? getHome() : "") + request.getServletPath().replace("/index.jsp", "");
    }

    public String getHome() {
        return getConfEnv().get("home", "/en");
    }

    public String getSites() {
        return getConfEnv().get("sites", getHome());
    }

    public boolean isRoot() {
        final String requestRoot = getRequestRoot();
        return UtilsText.isEmpty(requestRoot) || requestRoot.equals("/");
    }

    public boolean isHome() {
        return isRoot() || getRequestPath().equalsIgnoreCase(ConfEnv.getInstance().getHome());
    }

    public boolean isUnderHome() {
        return getRequestRoot().startsWith(ConfEnv.getInstance().getHome());
    }

    public String getContextPath() {
        return request.getContextPath();
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

    public String getContextServletPage() {
        return getContextPath() + getServletPage();
    }

    public String getRequestURI() {
        return request.getRequestURI();
    }

    public String getRequestUrl() {
        return getConfEnv().getUrl(getRequestURI());
    }

    public String getRequestPath() {
        final String contextPath = getContextPath();
        final String requestURI = getRequestURI();
        return requestURI.substring(contextPath.length());
    }

    public String getRequestRoot() {
        String[] paths = getRequestPath().split("/");
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
        return getRequestPath() + UtilsText.notEmptyUse(query, "?"+ query);
    }

    public String getContextUrlQuery() {
        return getContextPath(getRequestUrlQuery());
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

    public String getLanguage() {
        return UtilsText.notEmpty(getSessionAttribute("language"), "en");
    }

    public Locale getLocale() {
        return new Locale(getLanguage());
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
        String url = location.startsWith("http") ? location : getContextPath(location);
        if(isRedirect) {
            url = UtilsNetwork.addParameter(url, "redirect", getContextUrlQuery());
        }
        return url;
    }

    public RequestDispatcher getRequestDispatcher(final String path) {
        return request.getRequestDispatcher(path);
    }

    public void sendRedirect(final String location) throws IOException {
        response.sendRedirect(location);
    }

    public void forward(final String path) throws IOException, ServletException {
        getRequestDispatcher(path).forward(request, response);
    }

    public void include(final String path) throws IOException, ServletException {
        getRequestDispatcher(path).include(request, response);
    }

    public boolean isHostMatch() {
        return getConfEnv().getHost().equalsIgnoreCase(getRemoteHost());
    }

    public boolean isRemoteLocal() {
        final String remoteHost = request.getRemoteHost();
        return "0:0:0:0:0:0:0:1".equalsIgnoreCase(remoteHost) || "127.0.0.1".equalsIgnoreCase(remoteHost) || "localhost".equalsIgnoreCase(remoteHost);
    }

}
