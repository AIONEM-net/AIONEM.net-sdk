package aionem.net.sdk.web.dao;

import aionem.net.sdk.core.utils.UtilsText;
import aionem.net.sdk.data.beans.DaoRes;
import aionem.net.sdk.data.beans.Data;
import aionem.net.sdk.data.dao.Network;
import aionem.net.sdk.data.utils.UtilsResource;
import aionem.net.sdk.web.WebContext;
import aionem.net.sdk.web.config.ConfEnv;
import aionem.net.sdk.web.beans.Page;
import aionem.net.sdk.web.beans.Properties;
import aionem.net.sdk.web.beans.Resource;
import aionem.net.sdk.web.system.dao.DaoSysMinifierHtml;
import lombok.extern.log4j.Log4j2;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;


@Log4j2
public class PageManager {

    private static PageManager pageManager;
    public static PageManager getInstance() {
        if(pageManager == null) {
            pageManager = new PageManager();
        }
        return pageManager;
    }

    public PageManager() {

    }

    public Page getPageRoot() {
        return new Page("/");
    }

    public Page getHomePage(final Page page) {
        Page homePage = page;
        while(homePage != null && !homePage.isRoot() && homePage.hasParent()) {
            homePage = homePage.getParent();
        }
        return homePage;
    }

    public String getLanguage(final Page page) {

        String language = page.getLanguage();

        if(UtilsText.isEmpty(language)) {
            final Page homePage = getHomePage(page);
            language = homePage.getLanguage();
        }

        return language;
    }

    public boolean isRoot(final Page page) {
        return UtilsText.isEmpty(page.getUrl()) || page.getUrl().equals("/");
    }

    public boolean isHome(final Page page) {
        return isRoot(page) || page.getUrl().equalsIgnoreCase(ConfEnv.getInstance().getHome());
    }

    public boolean isSite(final Page page) {
        return ConfEnv.getInstance().getSites().contains(page.getPath());
    }

    public String getSite(final Page page) {
        final String site;
        if(isSite(page)) {
            site = page.getPath();
        }else {
            site = ConfEnv.getInstance().getHome();
        }
        return site;
    }

    public ArrayList<Page> getListPagesRoot() {
        final Page pageItem = getPageRoot();
        return getListPages(pageItem);
    }

    public ArrayList<Page> getListPages(final Page pageParent) {
        final ArrayList<Page> listPages = new ArrayList<>();
        
        final String rootPagePath = ResourceResolver.getRealPathPage();
        final Resource filePageParent = new Resource(ResourceResolver.getRealPathPage(pageParent.getPath()));
        for(final Resource filePage : getListResourcePages(filePageParent)) {
            final String pagePath = filePage.getRealPath().substring(rootPagePath.length());
            final Page page = new Page(pagePath, new Properties(filePage.child(Properties.PROPERTIES_JSON)));
            if(!pageParent.equals(page) && !listPages.contains(page)) {
                listPages.add(page);
            }
        }

        listPages.sort(comparatorPage);
        return listPages;
    }

    public ArrayList<Page> getListPagesAll() {
        final ArrayList<Page> listPages = new ArrayList<>();
        
        final String rootPagePath = ResourceResolver.getRealPathPage();
        for(final Resource filePage : getListResourcePagesAll()) {
            final String pagePath = filePage.getRealPath().substring(rootPagePath.length());
            final Page page = new Page(pagePath, new Properties(filePage.child(Properties.PROPERTIES_JSON)));
            if(!listPages.contains(page)) {
                listPages.add(page);
            }
        }

        listPages.sort(comparatorPage);
        return listPages;
    }

    public ArrayList<Resource> getListResourcePagesRoot() {
        return getListResourcePages(1);
    }

    public ArrayList<Resource> getListResourcePagesAll() {
        return getListResourcePages(-1);
    }

    public ArrayList<Resource> getListResourcePages(final int level) {
        final String realPathRoot = ResourceResolver.getRealPathPage();
        
        final ArrayList<Resource> listFilePages = new ArrayList<>();
        final Resource fileRoot = new Resource(realPathRoot);
        if(fileRoot.isFolder()) {
            getListResourcePages(fileRoot, listFilePages, level);
        }
        return listFilePages;
    }

    public ArrayList<Resource> getListResourcePages(final Page... pages) {
        final String[] pagePaths = new String[pages.length];
        for(int i = 0; i < pages.length; i++) {
            pagePaths[i] = pages[i].getPath();
        }
        return getListResourcePages(pagePaths);
    }

    public ArrayList<Resource> getListResourcePages(final String... pagePaths) {
        final Resource[] resourcePages = new Resource[pagePaths.length];
        for(int i = 0; i < pagePaths.length; i++) {
            resourcePages[i] = new Resource(ResourceResolver.getRealPathPage(pagePaths[i]));
        }
        return getListResourcePages(resourcePages);
    }

    private ArrayList<Resource> getListResourcePages(final Resource... resourcePages) {
        final ArrayList<Resource> listFilePagesAll = new ArrayList<>();
        
        final ArrayList<Resource> listFilePages = new ArrayList<>();
        for(Resource filePage : resourcePages) {
            if(!listFilePages.contains(filePage)) {
                listFilePagesAll.addAll(getListResourcePages(filePage, new ArrayList<>(), 1));
                listFilePages.add(filePage);
            }
        }
        return listFilePagesAll;
    }

    public ArrayList<Resource> getListResourcePagesAll(final Page... pages) {
        final String[] pagePaths = new String[pages.length];
        for(int i = 0; i < pages.length; i++) {
            pagePaths[i] = pages[i].getPath();
        }
        return getListResourcePagesAll(pagePaths);
    }

    public ArrayList<Resource> getListResourcePagesAll(final String... paths) {
        final Resource[] resourcePages = new Resource[paths.length];
        for(int i = 0; i < paths.length; i++) {
            resourcePages[i] = new Resource(ResourceResolver.getRealPathPage(paths[i]));
        }
        return getListResourcePagesAll(resourcePages);
    }

    private ArrayList<Resource> getListResourcePagesAll(final Resource... resourcePages) {
        final ArrayList<Resource> listFilePagesAll = new ArrayList<>();
        
        final ArrayList<Resource> listFilePages = new ArrayList<>();
        for(Resource filePage : resourcePages) {
            if(!listFilePages.contains(filePage)) {
                listFilePagesAll.addAll(getListResourcePages(filePage, new ArrayList<>(), -1));
                listFilePages.add(filePage);
            }
        }
        return listFilePagesAll;
    }

    private ArrayList<Resource> getListResourcePages(final Resource filePage, final ArrayList<Resource> listFilePages, final int level) {
        final ArrayList<Resource> files = filePage.children();
        if(files != null) {
            boolean hasProperties = false;
            boolean hasHtml = false;
            boolean hasJsp = false;
            files.sort(Comparator.comparing(Resource::getName));
            for(final Resource file : files) {
                if(file.isFolder()) {
                    if(level < 0) {
                        getListResourcePages(file, listFilePages, level);
                    } else if(level == 1) {
                        getListResourcePages(file, listFilePages, 0);
                    }
                }else {
                    final String fileName = file.getName();
                    if(fileName.equalsIgnoreCase(Properties.PROPERTIES_JSON)) {
                        hasProperties = true;
                    }else if(fileName.equalsIgnoreCase("index.html")) {
                        hasHtml = true;
                    }else if(fileName.equalsIgnoreCase("index.jsp")) {
                        hasJsp = true;
                    }
                }
            }
            if(hasProperties || hasHtml || hasJsp) {
                listFilePages.add(filePage);
            }
        }
        return listFilePages;
    }

    public void cache(final WebContext webContext, final boolean enabled) {
        if(enabled) {
            final long twoDaysInSeconds = 2*24*60*60;
            final long expiresTimeInSeconds = twoDaysInSeconds + (System.currentTimeMillis() / 1000);
            webContext.getResponse().setHeader("Cache-Control", "max-age=" + twoDaysInSeconds);
            webContext.getResponse().setDateHeader("Expires", expiresTimeInSeconds * 1000);

            cache(webContext);

        }else {
            webContext.getResponse().setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
            webContext.getResponse().setHeader("Pragma", "no-cache");
            webContext.getResponse().setDateHeader("Expires", 0);
        }
    }

    public void cache(final WebContext webContext) {
        final boolean isCaching = "true".equalsIgnoreCase(webContext.getHeader("A-Caching"));
        if(!isCaching && !webContext.isRemoteLocal()) {
            cache(webContext.getCurrentPage());
        }
    }

    public boolean cache(final Page page) {
        final String url = ConfEnv.getInstance().getUrl(page.getUrl());
        final Resource resourcePage = page.toResource();

        boolean isCached = false;

        final DaoRes resCache = new Network.Get(url)
                .setDataHeaders(new Data()
                        .put("A-Caching", "true")
                )
                .get();

        if(resCache.isSuccess() && resCache.hasResponse()) {
            final String html = DaoSysMinifierHtml.minify(resCache.getResponse());
            resourcePage.child("index.html").saveContent(html);
        }

        log.error("\nCache {} : {}", url, isCached);

        return isCached;
    }

    public ArrayList<String> invalidateCache() {
        final ArrayList<String> listPathPaths = new ArrayList<>();
        final List<Resource> listFilePages = getListResourcePagesAll();
        final String rootPagePath = ResourceResolver.getRealPathPage();
        for(final Resource filePage : listFilePages) {
            final Resource filePageHtml = new Resource(filePage, "index.html");
            if(filePageHtml.exists()) {
                final boolean deleted = filePageHtml.delete();
                if(deleted) {
                    final String pagePath = filePage.getRealPath().substring(rootPagePath.length());
                    listPathPaths.add(pagePath);
                }
            }
        }
        return listPathPaths;
    }

    public DaoRes move(final Page page, final String pathNew) {
        return move(page, pathNew, page.getName());
    }

    public DaoRes move(final Page page, final String pathNew, final String nameNew) {

        final DaoRes resMove = new DaoRes();

        try {

            if(!page.exists()) {
                resMove.setError("Page doesn't exist");
            }else {

                final String pathNameNew = UtilsResource.path(pathNew, nameNew);

                final Path pathSource = Paths.get(ResourceResolver.getRealPathPage(page.getPath()));
                final Path pathDestination = Paths.get(ResourceResolver.getRealPathPage(pathNameNew));

                Files.walkFileTree(pathSource, new SimpleFileVisitor<>() {

                    @Override
                    public FileVisitResult preVisitDirectory(final Path dir, final BasicFileAttributes attrs) throws IOException {
                        if (!dir.equals(pathSource)) {
                            return FileVisitResult.SKIP_SUBTREE;
                        }
                        final Path targetDir = pathDestination.resolve(pathSource.relativize(dir));
                        Files.createDirectories(targetDir);
                        return FileVisitResult.CONTINUE;
                    }

                    @Override
                    public FileVisitResult visitFile(final Path file, final BasicFileAttributes attrs) throws IOException {
                        final Path targetFile = pathDestination.resolve(pathSource.relativize(file));
                        Files.copy(file, targetFile, StandardCopyOption.REPLACE_EXISTING);
                        file.toFile().delete();
                        return FileVisitResult.CONTINUE;
                    }

                });

                final Page pageNew = new Page(pathNameNew);

                final int references = references(page, pageNew, true);

                resMove.setSuccess(true);
                resMove.put("references", references);
            }
            
        }catch(final Exception e) {
            resMove.setException(e);
        }
        
        return resMove;
    }

    public DaoRes copy(final Page page, final String pathNew) {
        return copy(page, pathNew, page.getName(), false);
    }

    public DaoRes copy(final Page page, final String pathNew, final boolean excludeChildren) {
        return copy(page, pathNew, page.getName(), excludeChildren);
    }

    public DaoRes copy(final Page page, final String pathNew, final String nameNew, final boolean excludeChildren) {

        final DaoRes resCopy = new DaoRes();

        try {

            if(!page.exists()) {
                resCopy.setError("Page doesn't exist");
            }else {

                final String pathNameNew = UtilsResource.path(pathNew, nameNew);

                final Path pathSource = Paths.get(ResourceResolver.getRealPathPage(page.getPath()));
                final Path pathDestination = Paths.get(ResourceResolver.getRealPathPage(pathNameNew));

                Files.walkFileTree(pathSource, new SimpleFileVisitor<>() {

                    @Override
                    public FileVisitResult preVisitDirectory(final Path dir, final BasicFileAttributes attrs) throws IOException {
                        if (excludeChildren && !dir.equals(pathSource)) {
                            return FileVisitResult.SKIP_SUBTREE;
                        }
                        final Path targetDir = pathDestination.resolve(pathSource.relativize(dir));
                        Files.createDirectories(targetDir);
                        return FileVisitResult.CONTINUE;
                    }

                    @Override
                    public FileVisitResult visitFile(final Path file, final BasicFileAttributes attrs) throws IOException {
                        final Path targetFile = pathDestination.resolve(pathSource.relativize(file));
                        Files.copy(file, targetFile, StandardCopyOption.REPLACE_EXISTING);
                        return FileVisitResult.CONTINUE;
                    }

                });

                final Page pageNew = new Page(pathNameNew);

                final int references = references(page, pageNew, pageNew, true);

                resCopy.setSuccess(true);
                resCopy.put("references", references);
            }

        }catch(final Exception e) {
            resCopy.setException(e);
        }

        return resCopy;
    }

    public int references(final Page page, final Page pageNew, final boolean update) {
        return references(page, pageNew, getPageRoot(), update);
    }

    public int references(final Page page, final Page pageNew, final Page pageSection, final boolean update) {
        final int[] totalReferences = {0};

        final Path pathSection = Paths.get(ResourceResolver.getRealPathPage(pageSection.getPath()));

        try {
            Files.walkFileTree(pathSection, new SimpleFileVisitor<>() {
                @Override
                public FileVisitResult visitFile(final Path file, final BasicFileAttributes attrs) {
                    if(Files.isRegularFile(file)) {
                        final int references = ResourceResolver.referencePages(new Resource(file), "ui.page"+ page.getPath(), "ui.page"+ pageNew.getPath(), update);
                        totalReferences[0] += references;
                    }
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (IOException e) {
            log.error("Error updating references: {}", e.toString());
        }

        return totalReferences[0];
    }
    
    public static Comparator<Page> comparatorPage = new Comparator<Page>() {
        @Override
        public int compare(final Page page1, final Page page2) {
            return Integer.compare(page1.getOrder(), page2.getOrder());
        }
    };
    
}
