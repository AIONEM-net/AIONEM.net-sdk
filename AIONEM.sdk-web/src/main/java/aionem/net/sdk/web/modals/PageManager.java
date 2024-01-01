package aionem.net.sdk.web.modals;

import aionem.net.sdk.core.utils.UtilsText;
import aionem.net.sdk.data.DaoRes;
import aionem.net.sdk.data.Data;
import aionem.net.sdk.data.Network;
import aionem.net.sdk.web.AioWeb;
import aionem.net.sdk.web.system.deploy.MinifierHtml;
import aionem.net.sdk.web.utils.UtilsWeb;
import lombok.extern.log4j.Log4j2;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@Log4j2
public class PageManager {

    public static final List<String> SYSTEM_PATH_1 = List.of("/ui.system", "/ui.page", "/ui.frontend", "/ui.drive", "/META-INF", "/WEB-INF");
    public static final List<String> SYSTEM_PATH_2 = List.of("/ui.config", "/ui.apps", "/ui.etc", "/ui.template");
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
        return new Page(aioWeb);
    }

    public Page getPageRoot() {
        return new Page(aioWeb, "/");
    }

    public ArrayList<Page> getListPagesRoot() {
        final Page pageItem = getPageRoot();
        return getListPages(pageItem);
    }

    public ArrayList<Page> getListPages(final Page pageParent) {
        final ArrayList<Page> listPages = new ArrayList<>();
        final String rootPagePath = aioWeb.getRealPathPage();
        final File filePageParent = new File(aioWeb.getRealPathPage(pageParent.getPath()));

        for(final File filePage : getListFilePages(filePageParent)) {
            final String pagePath = filePage.getAbsolutePath().substring(rootPagePath.length());
            final Page page = new Page(aioWeb, pagePath, new Properties(new File(filePage, "properties.json")));
            if(!pageParent.equals(page)) {
                listPages.add(page);
            }
        }
        return listPages;
    }

    public ArrayList<Page> getListPagesAll() {
        final ArrayList<Page> listPages = new ArrayList<>();
        final String rootPagePath = aioWeb.getRealPathPage();

        for(final File filePage : getListFilePagesAll()) {
            final String pagePath = filePage.getAbsolutePath().substring(rootPagePath.length());
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
        final String realPathRoot = aioWeb.getRealPathPage();
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
            filePages[i] = new File(aioWeb.getRealPathPage(pagePaths[i]));
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
            filePages[i] = new File(aioWeb.getRealPathPage(paths[i]));
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
        if(files != null) {
            boolean hasHtml = false;
            boolean hasJsp = false;
            Arrays.sort(files, Comparator.comparing(File::getName));
            for(final File file : files) {
                if(file.isDirectory()) {
                    if(level < 0) {
                        getListFilePages(file, listFilePages, level);
                    } else if(level == 1) {
                        getListFilePages(file, listFilePages, 0);
                    }
                }else {
                    final String fileName = file.getName();
                    if(fileName.equalsIgnoreCase("index.html")) {
                        hasHtml = true;
                    }else if(fileName.equalsIgnoreCase("index.jsp")) {
                        hasJsp = true;
                    }
                }
            }
            if(hasHtml || hasJsp) {
                listFilePages.add(filePage);
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
        return cache(env, aioWeb.getURI(page.getUrl()), aioWeb.getRealPathPage(page.getPath()));
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

        log.error("\nAioWeb::Cache {} : {} : {}", env, uri, isCached);

        return isCached;
    }

    public ArrayList<String> invalidateCache() {
        final ArrayList<String> listPathPaths = new ArrayList<>();
        final List<File> listFilePages = getListFilePagesAll();
        final String rootPagePath = aioWeb.getRealPathPage();
        for(final File filePage : listFilePages) {
            final File filePageHtml = new File(filePage, "index.html");
            if(filePageHtml.exists()) {
                final boolean deleted = filePageHtml.delete();
                if(deleted) {
                    final String pagePath = filePage.getAbsolutePath().substring(rootPagePath.length());
                    listPathPaths.add(pagePath);
                }
            }
        }
        return listPathPaths;
    }

    public boolean move(final Page page, final String pathNew) {
        return move(page, pathNew, page.getName());
    }

    public boolean move(final Page page, final String pathNew, final String nameNew) {
        try {
            final Path pathSource = Paths.get(aioWeb.getRealPathPage(page.getPath()));
            final Path pathDestination = Paths.get(aioWeb.getRealPathPage(pathNew + "/" + nameNew));

            if(!pathSource.toFile().exists()) {
                return false;
            }

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

            final Page pageNew = new Page(aioWeb, pathNew + "/" + nameNew);

            references(page, pageNew, true);

            return true;
        } catch (IOException e) {
            log.error("Error moving page {}", e.toString());
        }
        return false;
    }

    public boolean copy(final Page page, final String pathNew) {
        return copy(page, pathNew, page.getName(), false);
    }

    public boolean copy(final Page page, final String pathNew, final boolean excludeChildren) {
        return copy(page, pathNew, page.getName(), excludeChildren);
    }

    public boolean copy(final Page page, final String pathNew, final String nameNew, final boolean excludeChildren) {
        try {
            final Path pathSource = Paths.get(aioWeb.getRealPathPage(page.getPath()));
            final Path pathDestination = Paths.get(aioWeb.getRealPathPage(pathNew + "/" + nameNew));

            if(!pathSource.toFile().exists()) {
              return false;  
            }
            
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

            final Page pageNew = new Page(aioWeb, pathNew + "/" + nameNew);

            references(page, pageNew, pageNew, true);

            return true;
        } catch (IOException e) {
            log.error("Error copying page {}", e.toString());
        }
        return false;
    }

    public int references(final Page page, final Page pageNew, final boolean update) {
        return references(page, pageNew, getPageRoot(), update);
    }

    public int references(final Page page, final Page pageNew, final Page pageSection, final boolean update) {
        final int[] totalReferences = {0};

        final Path pathSection = Paths.get(aioWeb.getRealPathPage(pageSection.getPath()));

        try {
            Files.walkFileTree(pathSection, new SimpleFileVisitor<>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
                    if (Files.isRegularFile(file)) {
                        final int references = references(file, page.getPath(), pageNew.getPath(), update);
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

    private int references(final Path pathFile, final String pathOld, final String pathNew, final boolean update) {
        final int[] references = new int[]{-1};

        final Runnable runnable = new Runnable() {
            @Override
            public void run() {

                final String content = UtilsText.toString(pathFile);

                final String updatedContent = content.replace("ui.page"+ pathOld,"ui.page"+ pathNew);

                references[0] = (content.length() - updatedContent.length()) / (pathOld.length() - pathNew.length());

                if(update) {
                    UtilsWeb.writeFile(pathFile.toFile(), updatedContent);
                }

            }
        };

        if(update) {
            new Thread(runnable).start();
        }else {
            runnable.run();
        }

        return Math.abs(references[0]);
    }

}
