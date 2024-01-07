package aionem.net.sdk.web.system.dao;

import aionem.net.sdk.data.beans.Data;
import aionem.net.sdk.data.utils.UtilsJson;
import aionem.net.sdk.data.utils.UtilsResource;
import aionem.net.sdk.web.dao.PageManager;
import aionem.net.sdk.web.dao.ResourceResolver;
import aionem.net.sdk.web.beans.Page;
import aionem.net.sdk.web.beans.Resource;
import aionem.net.sdk.web.utils.UtilsWeb;
import lombok.extern.log4j.Log4j2;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;


@Log4j2
public class DaoSysDeploy {

    public static boolean deployWar(final String env, final String warFileName) {

        boolean isDeployedWar = false;

        final boolean isUpdatedEnv = updateEnv(env);

        if(isUpdatedEnv) {

            final ArrayList<Resource> listFilePagesCache = new PageManager().getListFilePagesAll(
                    "/en",
                    "/it",
                    "/rw",
                    "/dev",
                    "/auth/login",
                    "/auth/register"
            );

            final boolean isCachedAll = cacheAll(listFilePagesCache);

            final boolean isMinified = minify();

            if(isMinified && isCachedAll) {

                final File fileWar = buildWar(warFileName);
                final boolean isBuiltWar = fileWar != null && fileWar.exists();

                if(isBuiltWar) {

                    final boolean isUploadedWar = uploadWar(fileWar);

                    if(isUploadedWar) {
                        isDeployedWar = true;
                    }
                }

            }
        }

        return isDeployedWar;
    }

    public static boolean minify() {

        boolean isMinified = true;

        final File fileUiFrontend = new File(UtilsResource.getRealPathRoot("/ui.frontend"));

        if(fileUiFrontend.isDirectory()) {

            File[] files = fileUiFrontend.listFiles();

            if(files != null) {

                for (final File file : files) {

                    if(file.isDirectory()) {

                        final String css = DaoSysMinifierCss.minifyFolder(file, true);
                        final String js = DaoSysMinifierJs.minifyFolder(file, true);

                        UtilsWeb.writeFile(new File(file, ".css"), css);
                        UtilsWeb.writeFile(new File(file, ".js"), js);

                    }else {
                        if (file.getName().endsWith(".css")) {
                            DaoSysMinifierCss.minifyFile(file);

                        } else if (file.getName().equals(".js")) {
                            DaoSysMinifierJs.minifyFile(file);
                        }
                    }

                }

            }

        }

        return isMinified;
    }

    public static boolean cacheAll(final ArrayList<Resource> listFilePagesAll) {

        boolean isCachedAll = true;

        final String rootPagePath = ResourceResolver.getRealPathPage();

        for(final Resource filePage : listFilePagesAll) {
            final String pagePath = filePage.getRealPath().substring(rootPagePath.length() + 1);
            final Page page = new Page(pagePath);

            if(new Resource(filePage, "index.jsp").exists()) {
                final boolean isCached = new PageManager().cache(page);
                boolean isDeleted = true;
                if (isCached) {
                    final boolean isDeleted1 = new Resource(filePage, "content.jsp").delete();
                    final boolean isDeleted2 = new Resource(filePage, "properties.json").delete();
                    isDeleted = isDeleted1 || isDeleted2 || new Resource(filePage, "index.jsp").delete();
                }
                if(!isCached || !isDeleted) {
                    isCachedAll = false;
                    break;
                }
            }

        }

        return isCachedAll;
    }

    public static boolean updateEnv(final String env) {

        boolean isUpdated = false;
        int n = 0;

        try {

            final File fileConfEnv1 = new File(ResourceResolver.getRealPathWebInf("ui.config/application.json"));
            final File fileConfEnv2 = UtilsResource.getResourceFile("application.json");

            final ArrayList<File> listFileConfig = new ArrayList<>();
            listFileConfig.add(fileConfEnv1);
            listFileConfig.add(fileConfEnv2);

            for(final File fileConfEnv : listFileConfig) {
                if(fileConfEnv.exists() && fileConfEnv.isFile()) {
                    final Data data = new Data(fileConfEnv);
                    data.put("env", env);
                    isUpdated = UtilsWeb.writeFile(fileConfEnv, UtilsJson.prettyPrint(data.toJson()));
                    n++;
                }
            }

            final ArrayList<String> listResourceBundles = new ArrayList<>();
            listResourceBundles.add("application");

            for(final String resourceName : listResourceBundles) {

                final ResourceBundle resourceBundle = UtilsResource.getResourceBundle(resourceName);

                if(resourceBundle != null) {
                    final Data data = new Data(resourceBundle);
                    data.put("env", env);
                    isUpdated = UtilsWeb.writeFile(UtilsResource.getResourceFile(resourceName + ".properties"), data.toResourceBundleString());
                    n++;
                }
            }

        } catch (Exception e) {
            log.error("ERROR: AIONEM.net - JSP - WebApp : UpdateEnv :: {}", e.getMessage());
        }

        return isUpdated || n > 0;
    }

    public static File buildWar(final String warFileName) {

        final String webFileFolder = UtilsResource.getRealPathRoot();
        final File fileWebFolder = new File(webFileFolder);

        final String warFilePath = fileWebFolder.getParent() + "/" + (warFileName);
        final File fileWar = new File(warFilePath);

        final boolean isBuiltWar = buildWar(fileWebFolder, fileWar);

        return isBuiltWar ? fileWar : null;
    }

    public static boolean buildWar(final File fileWebFolder, final File fileWar) {

        boolean isBuilt = false;

        try {

            final boolean isEmpty;
            if(fileWar.exists()) {
                isEmpty = fileWar.delete();
            } else {
                isEmpty = true;
            }

            if(isEmpty) {

                final FileOutputStream fileOutputStream = new FileOutputStream(fileWar);
                final ZipOutputStream zipOutputStream = new ZipOutputStream(fileOutputStream);

                final File[] files = fileWebFolder.listFiles();

                if(files != null) {
                    for (final File file : files) {
                        createWar(file, file.getName(), zipOutputStream);
                    }
                }

                zipOutputStream.close();
                fileOutputStream.close();

                isBuilt = true;
            }

        } catch (Exception e) {
            log.error("Error: AIONEM.net - JSP - WebApp : deployWar", e);
        }

        return isBuilt;
    }

    public static void createWar(final File file, final String name, final ZipOutputStream zipOutputStream) throws IOException {

        if(file.isDirectory()) {
            final String path = name + (!name.endsWith("/") ? "/" : "");
            zipOutputStream.putNextEntry(new ZipEntry(path));
            zipOutputStream.closeEntry();
            final File[] children = file.listFiles();
            if(children != null) {
                for (final File fileChild : children) {
                    createWar(fileChild, path + fileChild.getName(), zipOutputStream);
                }
            }
            return;
        }

        final FileInputStream fileInputStream = new FileInputStream(file);
        final ZipEntry zipEntry = new ZipEntry(name);
        zipOutputStream.putNextEntry(zipEntry);

        final byte[] bytes = new byte[1024];
        int length;
        while((length = fileInputStream.read(bytes)) >= 0) {
            zipOutputStream.write(bytes, 0, length);
        }

        fileInputStream.close();
    }

    public static boolean uploadWar(final File fileWar) {

        boolean isUploaded = false;

        isUploaded = true;

        return isUploaded;
    }

}
