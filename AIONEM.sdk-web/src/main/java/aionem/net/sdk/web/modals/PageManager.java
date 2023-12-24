package aionem.net.sdk.web.modals;

import aionem.net.sdk.data.DaoRes;
import aionem.net.sdk.data.Data;
import aionem.net.sdk.data.Network;
import aionem.net.sdk.web.AioWeb;
import aionem.net.sdk.web.system.deploy.MinifierHtml;
import aionem.net.sdk.web.utils.UtilsWeb;
import lombok.extern.log4j.Log4j2;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


@Log4j2
public class PageManager {

    public static final List<String> SYSTEM_PATH_1 = List.of("/ui.system", "/ui.page", "/ui.frontend", "/ui.drive", "/META-INF", "/WEB-INF");
    public static final List<String> SYSTEM_PATH_2 = List.of("/ui.config", "/ui.apps", "/ui.content", "/ui.template");
    public static final List<String> SYSTEM_PATH_3 = List.of("/api", "/drive", "/assets", "/cdn");
    public static final List<String> SYSTEM_PATH = new ArrayList<>(SYSTEM_PATH_1);
    static {
        SYSTEM_PATH.addAll(SYSTEM_PATH_2);
        SYSTEM_PATH.addAll(SYSTEM_PATH_3);
    }

    private final AioWeb aioWeb;

    public PageManager(final AioWeb aioWeb) {
        this.aioWeb = aioWeb;
    }

    public Page getPage() {
        final File filePage = new File(aioWeb.getRealPathCurrent());
        return new Page(aioWeb, aioWeb.getServletPath(), new Properties(new File(filePage, "properties.json")));
    }

    public ArrayList<Page> getListPagesRoot() {
        final Page pageItem = new Page(aioWeb, "");
        return getListPages(pageItem);
    }

    public ArrayList<Page> getListPages(final Page pageParent) {
        final ArrayList<Page> listPages = new ArrayList<>();
        final String rootPagePath = aioWeb.getRealPathRoot();
        final File filePageParent = new File(aioWeb.getRealPathRoot(pageParent.getPath()));

        for(final File filePage : getListFilePages(filePageParent)) {
            final String pagePath = filePage.getAbsolutePath().substring(rootPagePath.length()-1);
            final Page page = new Page(aioWeb, pagePath, new Properties(new File(filePage, "properties.json")));
            if(!pageParent.equals(page)) {
                listPages.add(page);
            }
        }
        return listPages;
    }

    public ArrayList<Page> getListPagesAll() {
        final ArrayList<Page> listPages = new ArrayList<>();
        final String rootPagePath = aioWeb.getRealPathRoot();

        for(final File filePage : getListFilePagesAll()) {
            final String pagePath = filePage.getAbsolutePath().substring(rootPagePath.length()-1);
            final Page page = new Page(aioWeb, pagePath);
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
        final String realPathRoot = aioWeb.getRealPathRoot();
        final ArrayList<File> listFilePages = new ArrayList<>();
        final File fileRoot = new File(realPathRoot);
        if(fileRoot.isDirectory()) {
            getListFilePages(fileRoot, listFilePages, level);
        }
        return listFilePages;
    }

    public ArrayList<File> getListFilePages(final Page... pages) {
        final String[] pagePaths = new String[pages.length];
        for(int i = 0; i < pages.length; i++) {
            pagePaths[i] = pages[i].getPath();
        }
        return getListFilePages(pagePaths);
    }

    public ArrayList<File> getListFilePages(final String... pagePaths) {
        final File[] filePages = new File[pagePaths.length];
        for(int i = 0; i < pagePaths.length; i++) {
            filePages[i] = new File(aioWeb.getRealPathRoot(pagePaths[i]));
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

    public ArrayList<File> getListFilePagesAll(final Page... pages) {
        final String[] pagePaths = new String[pages.length];
        for(int i = 0; i < pages.length; i++) {
            pagePaths[i] = pages[i].getPath();
        }
        return getListFilePagesAll(pagePaths);
    }

    public ArrayList<File> getListFilePagesAll(final String... paths) {
        final File[] filePages = new File[paths.length];
        for(int i = 0; i < paths.length; i++) {
            filePages[i] = new File(aioWeb.getRealPathRoot(paths[i]));
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

    private ArrayList<File> getListFilePages(final File filePage, final ArrayList<File> listFilePages, final int level) {
        final File[] files = filePage.listFiles();
        if(!SYSTEM_PATH.contains("/"+ filePage.getName())) {
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

    public void cache(final boolean enabled) {
        if(enabled) {
            final long twoDaysInSeconds = 2*24*60*60;
            final long expiresTimeInSeconds = twoDaysInSeconds + (System.currentTimeMillis() / 1000);
            aioWeb.getResponse().setHeader("Cache-Control", "max-age=" + twoDaysInSeconds);
            aioWeb.getResponse().setDateHeader("Expires", expiresTimeInSeconds * 1000);

            checkToCache();

        }else {
            aioWeb.getResponse().setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
            aioWeb.getResponse().setHeader("Pragma", "no-cache");
            aioWeb.getResponse().setDateHeader("Expires", 0);
        }
    }

    public void checkToCache() {
        final boolean isCaching = "true".equalsIgnoreCase(aioWeb.getHeader("A-Caching"));
        if(!isCaching && !aioWeb.isLocal()) {
            cache(aioWeb.getURI(), aioWeb.getRealPathCurrent());
        }
    }

    public boolean cache(final Page page) {
        return cache("", page);
    }

    public boolean cache(final String env, final Page page) {
        return cache(env, aioWeb.getURI(page.getUrl()), aioWeb.getRealPathRoot(page.getPath()));
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
            final String html = MinifierHtml.minify(resCache.getResponse());
            isCached = UtilsWeb.writeFile(realPath +"/"+ "index.html", html);
        }

        return isCached;
    }

    public ArrayList<String> invalidateCache() {
        final ArrayList<String> listPathPaths = new ArrayList<>();
        final List<File> listFilePages = getListFilePagesAll();
        final String rootPagePath = aioWeb.getRealPathRoot();
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

}
