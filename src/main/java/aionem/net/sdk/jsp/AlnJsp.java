package aionem.net.sdk.jsp;

import aionem.net.sdk.api.AlnDaoRes;
import aionem.net.sdk.api.AlnNetwork;
import aionem.net.sdk.config.AlnConfig;
import aionem.net.sdk.data.AlnData;
import aionem.net.sdk.utils.AlnUtilsData;
import aionem.net.sdk.utils.AlnUtilsJsp;
import aionem.net.sdk.utils.AlnUtilsText;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;

import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.jsp.PageContext;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


@Log4j2
public @Getter class AlnJsp {

    protected HttpServletRequest request;
    protected HttpServletResponse response;
    protected PageContext pageContext;
    protected HttpSession session;

    private AlnJspConfig config;

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
        initConfig();
        return this;
    }

    public AlnJspConfig getConfig() {
        if(config == null) {
            config = new AlnJspConfig(this, "config");
            initConfig();
        }
        return config;
    }
    public void initConfig() {
        if(config == null) {
            AlnConfig.IS_DEBUG = getConfig().get("debug", AlnConfig.IS_DEBUG);
            AlnConfig.IS_DEBUG_EXCEPTION = getConfig().get("debug_exception", AlnConfig.IS_DEBUG_EXCEPTION);
        }
    }

    public String getConfigEnv() {
        return getConfigEnv(getInitParameter("env"));
    }
    public String getConfigDomain() {
        return getConfigEnv(getInitParameter("domain"));
    }
    public String getConfigUrl() {
        return getConfigEnv(getInitParameter("url"));
    }
    public String getConfigHost() {
        return getConfigEnv(getInitParameter("host"));
    }
    public String getConfigEnv(String env) {
        if(AlnConfig.ENV_LOCAL.equalsIgnoreCase(env)) {
            env = AlnConfig.ENV_LOCAL;
        }else if(AlnConfig.ENV_DEV.equalsIgnoreCase(env)) {
            env = AlnConfig.ENV_DEV;
        }else if(AlnConfig.ENV_STAGE.equalsIgnoreCase(env)) {
            env = AlnConfig.ENV_STAGE;
        }else if(AlnConfig.ENV_PROD.equalsIgnoreCase(env)) {
            env = AlnConfig.ENV_PROD;
        }else {
            env = AlnConfig.ENV_LOCAL;
        }
        return env;
    }

    public boolean isEnvProd() {
        return AlnConfig.ENV_PROD.equalsIgnoreCase(getConfigEnv());
    }
    public boolean isEnvStage() {
        return AlnConfig.ENV_STAGE.equalsIgnoreCase(getConfigEnv());
    }
    public boolean isEnvDev() {
        return AlnConfig.ENV_DEV.equalsIgnoreCase(getConfigEnv());
    }
    public boolean isEnvLocal() {
        return AlnConfig.ENV_LOCAL.equalsIgnoreCase(getConfigEnv());
    }

    public boolean isPublishMode() {
        return true;
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

    public <T> T getAttribute(final String name, final Object defaultValue) {
        return (T) AlnUtilsData.convert(getPageAttribute(name), defaultValue);
    }
    public <T> T getAttribute(final String name, final Class<T> type) {
        return AlnUtilsData.convert(getPageAttribute(name), type);
    }
    public Object getAttribute(final String name) {
        return getRequest().getAttribute(name);
    }

    public <T> T getPageAttribute(final String name, final Object defaultValue) {
        return (T) AlnUtilsData.convert(getPageAttribute(name), defaultValue);
    }
    public <T> T getPageAttribute(final String name, final Class<T> type) {
        return AlnUtilsData.convert(getPageAttribute(name), type);
    }
    public Object getPageAttribute(final String name) {
        return getPageContext().getAttribute(name, PageContext.PAGE_SCOPE);
    }

    public <T> T getApplicationAttribute(final String name, final Object defaultValue) {
        return (T) AlnUtilsData.convert(getApplicationAttribute(name), defaultValue);
    }
    public <T> T getApplicationAttribute(final String name, final Class<T> type) {
        return AlnUtilsData.convert(getApplicationAttribute(name), type);
    }
    public Object getApplicationAttribute(final String name) {
        return getPageContext().getAttribute(name, PageContext.APPLICATION_SCOPE);
    }

    public <T> T getSessionAttribute(final String name, final Object defaultValue) {
        return (T) AlnUtilsData.convert(getSessionAttribute(name), defaultValue);
    }
    public <T> T getSessionAttribute(final String name, final Class<T> type) {
        return AlnUtilsData.convert(getSessionAttribute(name), type);
    }
    public Object getSessionAttribute(final String name) {
        return getSession().getAttribute(name);
    }

    public static String name(final AlnJspCmp alnCmp) {
        return name(alnCmp.getClass());
    }
    public static String name(final Class<?> type) {
        return type.getPackageName() +"."+ type.getName();
    }
    public String value(final AlnJspCmp alnCmp) {
        return value(alnCmp.getProperties(), alnCmp.getClass());
    }
    public String value(final AlnJspCmp alnCmp, final Class<?> type) {
        return value(alnCmp.getProperties(), type);
    }
    public String value(final AlnJspCmp alnCmp, final String name) {
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
            getRequest().setAttribute(name, data);
        }
        getRequest().setAttribute(AlnJspProperties.PROPERTIES, data);
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
        return getRealPathRoot(getServletPath()) + (!AlnUtilsText.isEmpty(path) ? "/" + path : "");
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
        return getRequest().getServletPath().replace("/index.jsp", "");
    }
    public String getContextPath() {
        return getRequest().getContextPath();
    }
    public String getContextPath(final String path) {
        String contextPath = getContextPath() +"/"+ path;
        contextPath = contextPath.replace("//", "/");
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
        return getDomain() + getRequestURI();
    }
    public String getRequestUrl() {
        final String contextPath = getContextPath();
        final String requestURI = getRequestURI();
        return requestURI.substring(contextPath.length());
    }
    public String getRequestQuery() {
        return getRequest().getQueryString();
    }
    public String getRequestUrlQuery() {
        final String query = getRequestQuery();
        return getRequestUrl() + AlnUtilsText.notEmptyUse(query, "?"+ query);
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
        return AlnUtilsJsp.readResourceFile(this, fileName);
    }

    public String getHeader(final String name) {
        return getRequest().getHeader(name);
    }

    public String getLanguage() {
        return AlnUtilsText.notEmpty(getSessionAttribute("language"), "en");
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
        getResponse().sendRedirect(location);
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

            final AlnDaoRes resCache = new AlnNetwork.Get(getURI())
                    .setDataHeaders(new AlnData().put("A-Caching", "true"))
                    .get();

            if(resCache.isSuccess() && resCache.hasResponse()) {
                try(final FileWriter fileWriter = new FileWriter(getRealPathCurrent("index.html"), StandardCharsets.UTF_8)) {
                    fileWriter.write(resCache.getResponse());
                } catch (Exception e) {
                    log.error("checkToCache: "+ e);
                }
            }
        }
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

    public AlnJspPage getPage() {
        final File filePage = new File(getRealPathCurrent());
        return new AlnJspPage(this, getServletPath(), new AlnJspProperties(new File(filePage, "properties.json")));
    }
    public ArrayList<AlnJspPage> getListPagesRoot() {
        final AlnJspPage pageItem = new AlnJspPage(this, "");
        return getListPages(pageItem);
    }
    public ArrayList<AlnJspPage> getListPages(final AlnJspPage page) {
        final ArrayList<AlnJspPage> listPages = new ArrayList<>();
        final String rootPagePath = getRealPathRoot();
        final File filePage = new File(getRealPathRoot(page.getPath()));
        for(final File filePageItem : getListFilePages(filePage)) {
            final String pagePath = filePageItem.getAbsolutePath().substring(rootPagePath.length()-1);
            final AlnJspPage pageItem = new AlnJspPage(this, pagePath, new AlnJspProperties(new File(filePageItem, "properties.json")));
            if(!page.equals(pageItem)) {
                listPages.add(pageItem);
            }
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
    private ArrayList<File> getListFilePages(final File filePage) {
        return getListFilePages(filePage, new ArrayList<>(), 1);
    }
    public static final List<String> SYSTEM_PATH = List.of("ui.config", "ui.apps", "ui.dam", "ui.content", "ui.frontend", "META-INF", "WEB-INF");
    private ArrayList<File> getListFilePages(final File filePage, final ArrayList<File> listFilePages, final int level) {
        final File[] files = filePage.listFiles();
        if(!SYSTEM_PATH.contains(filePage.getName())) {
            if (files != null) {
                boolean hasHtml = false;
                boolean hasJsp = false;
                for (final File file : files) {
                    if (file.isDirectory()) {
                        if (level < 0) {
                            getListFilePages(file, listFilePages, level);
                        } else if (level == 1) {
                            getListFilePages(file, listFilePages, 0);
                        }
                    } else {
                        final String fileName = file.getName();
                        if (fileName.equalsIgnoreCase("index.html")) {
                            hasHtml = true;
                        } else if (fileName.equalsIgnoreCase("index.jsp")) {
                            hasJsp = true;
                        }
                    }
                }
                if (hasHtml || hasJsp) {
                    listFilePages.add(filePage);
                }
            }
        }
        return listFilePages;
    }

}
