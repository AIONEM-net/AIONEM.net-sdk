package aionem.net.sdk.jsp;

import aionem.net.sdk.api.AlnDaoRes;
import aionem.net.sdk.api.AlnNetwork;
import aionem.net.sdk.data.AlnData;
import aionem.net.sdk.utils.AlnTextUtils;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;

import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.jsp.PageContext;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Locale;


@Log4j2
public @Getter class AlnJsp {

    protected HttpServletRequest request;
    protected HttpServletResponse response;
    protected PageContext pageContext;
    protected HttpSession session;

    public AlnJsp() {

    }
    public AlnJsp(final ServletRequest request, final ServletResponse response) {
        init((HttpServletRequest) request, (HttpServletResponse) response);
    }
    public AlnJsp(final HttpServletRequest request, final HttpServletResponse response) {
        init(request, response);
    }

    public AlnJsp init(final HttpServletRequest request, final HttpServletResponse response) {
        return init(request, response, null);
    }
    public AlnJsp init(final HttpServletRequest request, final HttpServletResponse response, final PageContext pageContext) {
        this.request = request;
        this.response = response;
        this.pageContext = pageContext;
        this.session = request.getSession(true);
        return this;
    }

    public void setAttribute(final Object value) {
        setAttribute(AlnJspProperties.PROPERTIES, value);
    }
    public void setAttribute(final AlnData data) {
        setAttribute(name(data.getClass()), data.toString());
    }
    public void setAttribute(final String name, Object value) {
        request.setAttribute(name, value);
    }

    public <T> T getAttribute(final Object object) {
        return (T) getAttribute(object.getClass());
    }
    public <T> T getAttribute(final Class<T> type) {
        return (T) getAttribute(name(type));
    }
    public Object getAttribute(final String name) {
        return request.getAttribute(name);
    }

    public static String name(final AlnCmp alnCmp) {
        return name(alnCmp.getClass());
    }
    public static String name(final Class<?> type) {
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
        if(!AlnJspProperties.PROPERTIES.equals(name)) {
            request.setAttribute(name, data);
        }
        request.setAttribute(AlnJspProperties.PROPERTIES, data);
        return data.toString();
    }

    public ServletContext getServletContext() {
        return request.getServletContext();
    }

    public String getRealPathCurrent() {
        return getRealPathCurrent("");
    }
    public String getRealPathCurrent(final String path) {
        return getRealPathRoot(getServletPath()) + (!AlnTextUtils.isEmpty(path) ? "/" + path : "");
    }
    public String getRealPathRoot() {
        return getRealPathRoot("");
    }
    public String getRealPathRoot(final String path) {
        String realPath = getServletContext().getRealPath(path);
        realPath = realPath.replace("//", "/");
        return realPath;
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
    public String getURI() {
        final String domain = !isLocal() ? getRemoteHost() : "127.0.0.1";
        return (request.isSecure() ? "https" : "http") +"://"+ "127.0.0.1" +":"+ getServerPort() + getRequestURI();
    }
    public String getRequestUrl() {
        final String contextPath = getContextPath();
        final String requestURI = getRequestURI();
        return requestURI.substring(contextPath.length());
    }
    public String getRequestQuery() {
        return request.getQueryString();
    }
    public String getRequestUrlQuery() {
        final String query = getRequestQuery();
        return getRequestUrl() + AlnTextUtils.notEmptyUse(query, "?"+ query);
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

    public ClassLoader getClassLoader() {
        return response.getClass().getClassLoader();
    }
    public InputStream getResourceAsStream(final String name) {
        return getClassLoader().getResourceAsStream(name);
    }

    public String getHeader(final String name) {
        return request.getHeader(name);
    }

    public String getLanguage() {
        return AlnTextUtils.notEmpty(session.getAttribute("language"), "en");
    }

    public Locale getLocale() {
        return new Locale(getLanguage());
    }

    public boolean isEdit() {
        return false;
    }

    public boolean isDisabled() {
        return true;
    }

    public void sendRedirect(final String location) throws IOException {
        response.sendRedirect(location);
    }

    public boolean isLocal() {
        final String remoteHost = request.getRemoteHost();
        return "0:0:0:0:0:0:0:1".equalsIgnoreCase(remoteHost) || "127.0.0.1".equalsIgnoreCase(remoteHost) || "localhost".equalsIgnoreCase(remoteHost);
    }


    public void cache(final boolean enabled) {
        if(enabled) {
            final long twoDaysInSeconds = 2*24*60*60;
            final long expiresTimeInSeconds = twoDaysInSeconds + (System.currentTimeMillis() / 1000);
            response.setHeader("Cache-Control", "max-age=" + twoDaysInSeconds);
            response.setDateHeader("Expires", expiresTimeInSeconds * 1000);

            checkToCache();

        } else {
            response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
            response.setHeader("Pragma", "no-cache");
            response.setDateHeader("Expires", 0);
        }
    }

    public void checkToCache() {

        final boolean isCaching = "true".equalsIgnoreCase(getHeader("A-Caching"));

        if(!isCaching) {

            final AlnDaoRes resCache = new AlnNetwork.Get(getURI())
                    .setDataHeaders(new AlnData().put("A-Caching", "true"))
                    .get();

            if(resCache.isSuccess() && resCache.hasResponse()) {
                try(FileWriter writer = new FileWriter(getRealPathCurrent("index.html"), Charset.defaultCharset())) {
                    writer.write(resCache.getResponse());
                } catch (Exception e) {
                    log.error("checkToCache: "+ e);
                }
            }
        }
    }
    
}
