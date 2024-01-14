package aionem.net.sdk.web.dao;

import aionem.net.sdk.core.utils.UtilsText;
import aionem.net.sdk.data.utils.UtilsResource;
import aionem.net.sdk.web.config.ConfEnv;
import aionem.net.sdk.web.beans.Resource;
import lombok.extern.log4j.Log4j2;

import java.io.FilenameFilter;
import java.io.InputStream;
import java.util.*;


@Log4j2
public class ResourceResolver {

    public static final List<String> SYSTEM_PATH_ROOTS = List.of("/ui.system", "/ui.page", "/ui.frontend", "/ui.drive", "/WEB-INF", "/META-INF");
    public static final List<String> SYSTEM_PATH_WEB_INF = List.of("/ui.config", "/ui.apps", "/ui.template");
    public static final List<String> SYSTEM_PATH_ALIAS = List.of("/api", "/drive", "/assets", "/cdn");
    public static final String DRIVE_PATH_UPLOADS = "/ui.drive/uploads/";

    public static final List<String> SYSTEM_PATHS = new ArrayList<>(SYSTEM_PATH_ROOTS);

    static {
        ResourceResolver.SYSTEM_PATHS.addAll(ResourceResolver.SYSTEM_PATH_WEB_INF);
        ResourceResolver.SYSTEM_PATHS.addAll(ResourceResolver.SYSTEM_PATH_ALIAS);
    }

    public static boolean isSystemPath(final String path) {
        return isSystemPath(SYSTEM_PATHS, path);
    }

    public static boolean isSystemPathRoot(final String path) {
        return isSystemPath(SYSTEM_PATH_ROOTS, path);
    }

    public static boolean isSystemPathAlias(final String path) {
        return isSystemPath(SYSTEM_PATH_ALIAS, path);
    }

    public static boolean isSystemPath(final List<String> paths, final String path) {
        boolean isSystemPath = false;
        for(final String systemPath: paths) {
            isSystemPath = path.startsWith(systemPath);
            if(isSystemPath) break;
        }
        return isSystemPath;
    }

    public static String getRealPathWebInf() {
        return getRealPathWebInf("");
    }

    public static String getRealPathWebInf(final String path) {
        return UtilsResource.getRealPathParent(path);
    }

    public static Resource getRealFileWebInf(final String path) {
        return new Resource(getRealPathWebInf(path));
    }

    public static String getRealPathPage() {
        return UtilsResource.getRealPathRoot("/ui.page");
    }

    public static String getRealPathDrive() {
        return UtilsResource.getRealPathRoot("/ui.drive");
    }

    public static String getRealPathDrive(final String path) {
        return UtilsResource.getRealPathRoot(UtilsResource.path("/ui.drive", path));
    }

    public static String getRealPathPage(final String path) {
        return UtilsResource.getRealPathRoot(UtilsResource.path("/ui.page", (!UtilsText.isEmpty(path) && !path.equals("/") ? path : ConfEnv.getInstance().getHome())));
    }

    public static Resource getResourcePage(final String path) {
        return new Resource(getRealPathPage(path));
    }

    public static Resource getResourceDrive(final String path) {
        return new Resource(getRealPathDrive(path));
    }

    public static ArrayList<Resource> findResources(final Resource resourceFolder, final FilenameFilter filenameFilter) {

        final ArrayList<Resource> listResources = new ArrayList<>();

        if(resourceFolder.isFolder()) {
            for(final Resource resource : resourceFolder.children()) {
                if(resource.isFolder()) {
                    listResources.addAll(findResources(resource, filenameFilter));
                }else {
                    if(filenameFilter.accept(resourceFolder.getFile(), resource.getName())) {
                        listResources.add(resource);
                    }
                }
            }
        }

        return listResources;
    }

    public static String readResource(final String name) {
        return readResource(name, "/WEB-INF/classes/", "/classes/");
    }

    public static String readResourceWebInf(final String name) {
        return readResource(name, "/WEB-INF/", "/");
    }

    public static String readResourceConfig(final String name) {
        return readResource(name, "/WEB-INF/ui.config/", "/ui.config/");
    }

    public static String readResourceEnv(final String name) {
        return readResource(name, "/WEB-INF/ui.config/env/", "/ui.config/env/");
    }

    public static String readResourceI18n(final String name) {
        return readResource(name, "/WEB-INF/ui.config/i18n/", "/ui.config/i18n/");
    }

    public static String readResourceEtc(final String name) {
        return readResource(name, "/WEB-INF/ui.config/etc/", "/ui.config/etc/");
    }

    public static ResourceBundle getResourceBundleEnv(final String name) {
        return UtilsResource.getResourceBundle("/ui.config/env/" + name);
    }

    public static ResourceBundle getResourceBundleI18n(final String name) {
        return UtilsResource.getResourceBundle("/ui.config/i18n/" + name);
    }

    public static ResourceBundle getResourceBundleI18n(final String name, final Locale locale) {
        return UtilsResource.getResourceBundle("/ui.config/i18n/" + name, locale);
    }

    public static ResourceBundle getResourceBundleEtc(final String name) {
        return UtilsResource.getResourceBundle("/ui.config/etc/" + name);
    }

    public static String readResource(final String name, final String... folders) {
        final InputStream inputStream = getResourceStream(name, folders);
        if(inputStream != null) {
            return UtilsText.toString(inputStream);
        }else  {
            return null;
        }
    }

    public static InputStream getResourceStream(final String name, String... folders) {
        try {

            if(folders == null || folders.length == 0) folders = new String[]{""};

            for(final String folder : folders) {

                Resource resource = new Resource(UtilsResource.getRealPathRoot(folder + name));
                if(resource.exists() && resource.isFile()) {
                    return resource.getInputStream();
                }

                InputStream inputStream = UtilsResource.getResourceStreamOrParent(name, folder);
                if(inputStream != null) {
                    return inputStream;
                }
            }

        }catch(Exception e) {
            log.error("\nERROR: - readStream ::" + e + Arrays.toString(folders) + name +"\n");
        }
        return null;
    }

    public static int referencePages(final Resource resource, final String pathOld, final String pathNew, final boolean update) {
        final int[] references = new int[]{-1};

        final Runnable runnable = new Runnable() {
            @Override
            public void run() {

                final String content = resource.readContent();

                final String updatedContent = content.replace("ui.page"+ pathOld,"ui.page"+ pathNew);

                references[0] = (content.length() - updatedContent.length()) / (pathOld.length() - pathNew.length());

                if(update) {
                    resource.saveContent(updatedContent);
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

    public static int referenceDrives(final Resource resource, final String pathOld, final String pathNew, final boolean update) {
        final int[] references = new int[]{-1};

        final Runnable runnable = new Runnable() {
            @Override
            public void run() {

                final String content = resource.readContent();

                final String updatedContent = content.replace("ui.drive"+ pathOld,"ui.drive"+ pathNew);

                references[0] = (content.length() - updatedContent.length()) / (pathOld.length() - pathNew.length());

                if(update) {
                    resource.saveContent(updatedContent);
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
