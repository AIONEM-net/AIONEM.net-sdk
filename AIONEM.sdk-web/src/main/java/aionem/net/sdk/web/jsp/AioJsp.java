package aionem.net.sdk.web.jsp;

import aionem.net.sdk.core.utils.UtilsConverter;
import aionem.net.sdk.data.api.DaoRes;
import aionem.net.sdk.data.net.Network;
import aionem.net.sdk.core.config.Env;
import aionem.net.sdk.data.Data;
import aionem.net.sdk.core.utils.UtilsNetwork;
import aionem.net.sdk.core.utils.UtilsText;
import aionem.net.sdk.web.jsp.deploy.JspMinifierHtml;
import aionem.net.sdk.web.jsp.modals.JspCmp;
import aionem.net.sdk.web.jsp.modals.JspConfig;
import aionem.net.sdk.web.jsp.modals.JspPage;
import aionem.net.sdk.web.jsp.modals.JspProperties;
import aionem.net.sdk.web.jsp.utils.JspConstants;
import aionem.net.sdk.web.jsp.utils.JspUtils;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import javax.servlet.jsp.PageContext;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


@Log4j2
public @Getter class AioJsp {

    protected HttpServletRequest request;
    protected HttpServletResponse response;
    protected PageContext pageContext;
    protected HttpSession session;
    protected String contextPath;

    private JspConfig config;

    public AioJsp() {

    }
    public AioJsp(final ServletRequest request, final ServletResponse response) {
        init((HttpServletRequest) request, (HttpServletResponse) response);
    }
    public AioJsp(final HttpServletRequest request, final HttpServletResponse response) {
        init(request, response);
    }

    public AioJsp init(final HttpServletRequest request, final HttpServletResponse response) {
        return init(request, response, null);
    }
    public AioJsp init(final HttpServletRequest request, final HttpServletResponse response, final PageContext pageContext) {
        this.request = request;
        this.response = response;
        this.pageContext = pageContext;
        this.session = request.getSession(true);
        this.contextPath = getConfig().get("contextPath", request.getContextPath());
        return this;
    }

    public JspConfig getConfig() {
        if(config == null) {
            config = new JspConfig(this, "config");

            Env.IS_DEBUG = config.get("debug", Env.IS_DEBUG);
            Env.IS_DEBUG_EXCEPTION = config.get("debug_exception", Env.IS_DEBUG_EXCEPTION);
        }
        return config;
    }

    public String getConfigEnv() {
        final String envRequest = getRequest().getHeader("A-Env");
        final String envWebApp = getInitParameter("env");
        return getConfigEnv(UtilsText.notEmpty(envRequest, envWebApp));
    }
    public String getConfigSenderID() {
        return getConfig().get("senderID");
    }
    public String getConfigDomain() {
        return getConfig().get("domain");
    }
    public String getConfigUrl() {
        return getConfig().get("url");
    }
    public String getConfigUrl(final String path) {
        return getConfigUrl() + getContextPath(path);
    }
    public String getConfigHost() {
        return getConfig().get("host");
    }
    public String getConfigSenderName() {
        return getConfig().get("senderName");
    }
    public String getConfigSenderEmail() {
        return getConfig().get("senderEmail");
    }
    public String getConfigSenderPassword() {
        return getConfig().get("senderPassword");
    }
    public String getConfigSupportEmail() {
        return getConfig().get("supportEmail");
    }
    public String getConfigCcEmail() {
        return getConfig().get("ccEmail");
    }
    public String getInitGoogleSignInClientID() {
        return getInitParameter("GoogleSignInClientID");
    }
    public String getConfigAPI_SMS_KEY() {
        return getConfig().get("API_SMS_KEY");
    }
    public String getConfigEnv(String env) {
        if(JspConstants.ENV_LOCAL.equalsIgnoreCase(env)) {
            env = JspConstants.ENV_LOCAL;
        }else if(JspConstants.ENV_DEV.equalsIgnoreCase(env)) {
            env = JspConstants.ENV_DEV;
        }else if(JspConstants.ENV_STAGE.equalsIgnoreCase(env)) {
            env = JspConstants.ENV_STAGE;
        }else if(JspConstants.ENV_PROD.equalsIgnoreCase(env)) {
            env = JspConstants.ENV_PROD;
        }else {
            env = JspConstants.ENV_LOCAL;
        }
        return env;
    }

    public boolean isEnvProd() {
        return JspConstants.ENV_PROD.equalsIgnoreCase(getConfigEnv());
    }
    public boolean isEnvStage() {
        return JspConstants.ENV_STAGE.equalsIgnoreCase(getConfigEnv());
    }
    public boolean isEnvDev() {
        return JspConstants.ENV_DEV.equalsIgnoreCase(getConfigEnv());
    }
    public boolean isEnvLocal() {
        return JspConstants.ENV_LOCAL.equalsIgnoreCase(getConfigEnv());
    }

    public boolean isPublishMode() {
        return true;
    }

    public void setAttribute(final Object value) {
        setAttribute(JspProperties.PROPERTIES, value);
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

    public static String name(final JspCmp jspCmp) {
        return name(jspCmp.getClass());
    }
    public static String name(final Class<?> type) {
        return type.getPackageName() +"."+ type.getName();
    }
    public String value(final JspCmp jspCmp) {
        return value(jspCmp.getProperties(), jspCmp.getClass());
    }
    public String value(final JspCmp jspCmp, final Class<?> type) {
        return value(jspCmp.getProperties(), type);
    }
    public String value(final JspCmp jspCmp, final String name) {
        return value(jspCmp.getProperties(), name);
    }
    public String value(final JspProperties properties, final Class<?> type) {
        return value(properties.getData(), type);
    }
    public String value(final JspProperties properties, final String name) {
        return value(properties.getData(), name);
    }
    public String value(final Data data, final Class<?> type) {
        return value(data, name(type));
    }
    public String value(final Object data, final String name) {
        if(!JspProperties.PROPERTIES.equals(name)) {
            getRequest().setAttribute(name, data);
        }
        getRequest().setAttribute(JspProperties.PROPERTIES, data);
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
        final String domain = isLocal() ? "127.0.0.1" : getConfigDomain();
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
        return getResponse().getClass().getClassLoader();
    }
    public InputStream getResourceAsStream(final String name) {
        return getClassLoader().getResourceAsStream(name);
    }
    public PrintWriter getWriter() throws IOException {
        return getResponse().getWriter();
    }
    public String readFile(final String fileName) {
        return JspUtils.readResourceFile(this, fileName);
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
        return getConfigHost().equalsIgnoreCase(getRemoteHost());
    }
    public boolean isLocal() {
        final String remoteHost = getRequest().getRemoteHost();
        return "0:0:0:0:0:0:0:1".equalsIgnoreCase(remoteHost) || "127.0.0.1".equalsIgnoreCase(remoteHost) || "localhost".equalsIgnoreCase(remoteHost);
    }

    public void cache(final boolean enabled) {
        if(enabled) {
            final long twoDaysInSeconds = 2*24*60*60;
            final long expiresTimeInSeconds = twoDaysInSeconds + (System.currentTimeMillis() / 1000);
            getResponse().setHeader("Cache-Control", "max-age=" + twoDaysInSeconds);
            getResponse().setDateHeader("Expires", expiresTimeInSeconds * 1000);

            checkToCache();

        }else {
            getResponse().setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
            getResponse().setHeader("Pragma", "no-cache");
            getResponse().setDateHeader("Expires", 0);
        }
    }

    public void checkToCache() {
        final boolean isCaching = "true".equalsIgnoreCase(getHeader("A-Caching"));
        if(!isCaching && !isLocal()) {
            cache(getURI(), getRealPathCurrent());
        }
    }

    public boolean cache(final JspPage page) {
        return cache("", page);
    }
    public boolean cache(final String env, final JspPage page) {
        return cache(env, getURI(page.getUrl()), getRealPathRoot(page.getPath()));
    }
    public boolean cache(final String uri, final String realPath) {
        return cache("", uri, realPath);
    }
    public boolean cache(final String env, final String uri, final String realPath) {
        boolean isCached = false;

        final DaoRes resCache = new Network.Get(uri)
                .setDataHeaders(new Data()
                        .put("A-Caching", "true")
                        .put("A-Env", env)
                )
                .get();

        if(resCache.isSuccess() && resCache.hasResponse()) {
            final String html = JspMinifierHtml.minify(resCache.getResponse());
            isCached = JspUtils.writeFile(realPath +"/"+ "index.html", html);
        }

        return isCached;
    }

    public ArrayList<String> invalidateCache() {
        final ArrayList<String> listPathPaths = new ArrayList<>();
        final List<File> listFilePages = getListFilePagesAll();
        final String rootPagePath = getRealPathRoot();
        for(final File filePage : listFilePages) {
            final File filePageHtml = new File(filePage, "index.html");
            if(filePageHtml.exists()) {
                final boolean deleted = filePageHtml.delete();
                if(deleted) {
                    final String pagePath = filePage.getAbsolutePath().substring(rootPagePath.length()-1);
                    listPathPaths.add(pagePath);
                }
            }
        }
        return listPathPaths;
    }

    public JspPage getPage() {
        final File filePage = new File(getRealPathCurrent());
        return new JspPage(this, getServletPath(), new JspProperties(new File(filePage, "properties.json")));
    }
    public ArrayList<JspPage> getListPagesRoot() {
        final JspPage pageItem = new JspPage(this, "");
        return getListPages(pageItem);
    }
    public ArrayList<JspPage> getListPages(final JspPage pageParent) {
        final ArrayList<JspPage> listPages = new ArrayList<>();
        final String rootPagePath = getRealPathRoot();
        final File filePageParent = new File(getRealPathRoot(pageParent.getPath()));

        for(final File filePage : getListFilePages(filePageParent)) {
            final String pagePath = filePage.getAbsolutePath().substring(rootPagePath.length()-1);
            final JspPage page = new JspPage(this, pagePath, new JspProperties(new File(filePage, "properties.json")));
            if(!pageParent.equals(page)) {
                listPages.add(page);
            }
        }
        return listPages;
    }
    public ArrayList<JspPage> getListPagesAll() {
        final ArrayList<JspPage> listPages = new ArrayList<>();
        final String rootPagePath = getRealPathRoot();

        for(final File filePage : getListFilePagesAll()) {
            final String pagePath = filePage.getAbsolutePath().substring(rootPagePath.length()-1);
            final JspPage page = new JspPage(this, pagePath);
            listPages.add(page);
        }
        return listPages;
    }

    public ArrayList<File> getListFilePagesRoot() {
        return getListFilePages(1);
    }
    public ArrayList<File> getListFilePagesAll() {
        return getListFilePages(-1);
    }
    public ArrayList<File> getListFilePages(final int level) {
        final String realPathRoot = getRealPathRoot();
        final ArrayList<File> listFilePages = new ArrayList<>();
        final File fileRoot = new File(realPathRoot);
        if(fileRoot.isDirectory()) {
            getListFilePages(fileRoot, listFilePages, level);
        }
        return listFilePages;
    }
    public ArrayList<File> getListFilePages(final JspPage... pages) {
        final String[] pagePaths = new String[pages.length];
        for(int i = 0; i < pages.length; i++) {
            pagePaths[i] = pages[i].getPath();
        }
        return getListFilePages(pagePaths);
    }
    public ArrayList<File> getListFilePages(final String... pagePaths) {
        final File[] filePages = new File[pagePaths.length];
        for(int i = 0; i < pagePaths.length; i++) {
            filePages[i] = new File(getRealPathRoot(pagePaths[i]));
        }
        return getListFilePages(filePages);
    }
    private ArrayList<File> getListFilePages(final File... filePages) {
        final ArrayList<File> listFilePagesAll = new ArrayList<>();
        final ArrayList<File> listFilePages = new ArrayList<>();
        for(File filePage : filePages) {
            if(!listFilePages.contains(filePage)) {
                listFilePagesAll.addAll(getListFilePages(filePage, new ArrayList<>(), 1));
                listFilePages.add(filePage);
            }
        }
        return listFilePagesAll;
    }
    public ArrayList<File> getListFilePagesAll(final JspPage... pages) {
        final String[] pagePaths = new String[pages.length];
        for(int i = 0; i < pages.length; i++) {
            pagePaths[i] = pages[i].getPath();
        }
        return getListFilePagesAll(pagePaths);
    }
    public ArrayList<File> getListFilePagesAll(final String... paths) {
        final File[] filePages = new File[paths.length];
        for(int i = 0; i < paths.length; i++) {
            filePages[i] = new File(getRealPathRoot(paths[i]));
        }
        return getListFilePagesAll(filePages);
    }
    private ArrayList<File> getListFilePagesAll(final File... filePages) {
        final ArrayList<File> listFilePagesAll = new ArrayList<>();
        final ArrayList<File> listFilePages = new ArrayList<>();
        for(File filePage : filePages) {
            if(!listFilePages.contains(filePage)) {
                listFilePagesAll.addAll(getListFilePages(filePage, new ArrayList<>(), -1));
                listFilePages.add(filePage);
            }
        }
        return listFilePagesAll;
    }
    public static final List<String> SYSTEM_PATH = List.of("ui.admin", "ui.config", "ui.apps", "ui.dam", "ui.content", "ui.frontend", "ui.drive", "i18n", "META-INF", "WEB-INF");
    private ArrayList<File> getListFilePages(final File filePage, final ArrayList<File> listFilePages, final int level) {
        final File[] files = filePage.listFiles();
        if(!SYSTEM_PATH.contains(filePage.getName())) {
            if(files != null) {
                boolean hasHtml = false;
                boolean hasJsp = false;
                for (final File file : files) {
                    if(file.isDirectory()) {
                        if(level < 0) {
                            getListFilePages(file, listFilePages, level);
                        } else if(level == 1) {
                            getListFilePages(file, listFilePages, 0);
                        }
                    } else {
                        final String fileName = file.getName();
                        if(fileName.equalsIgnoreCase("index.html")) {
                            hasHtml = true;
                        } else if(fileName.equalsIgnoreCase("index.jsp")) {
                            hasJsp = true;
                        }
                    }
                }
                if(hasHtml || hasJsp) {
                    listFilePages.add(filePage);
                }
            }
        }
        return listFilePages;
    }

}
