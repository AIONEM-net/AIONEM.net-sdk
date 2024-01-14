package aionem.net.sdk.web.system.dao;

import aionem.net.sdk.data.beans.Data;
import aionem.net.sdk.data.utils.UtilsJson;
import aionem.net.sdk.data.utils.UtilsResource;
import aionem.net.sdk.web.beans.Page;
import aionem.net.sdk.web.beans.Properties;
import aionem.net.sdk.web.beans.Resource;
import aionem.net.sdk.web.dao.PageManager;
import aionem.net.sdk.web.dao.ResourceResolver;
import lombok.extern.log4j.Log4j2;

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

            final ArrayList<Resource> listFilePagesCache = new PageManager().getListResourcePagesAll(
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

                final Resource fileWar = buildWar(warFileName);
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

        final Resource fileUiFrontend = new Resource(UtilsResource.getRealPathRoot("/ui.frontend"));

        if(fileUiFrontend.isFolder()) {

            for (final Resource file : fileUiFrontend.children()) {

                if(file.isFolder()) {

                    final String css = DaoSysMinifierCss.minifyFolder(file, true);
                    final String js = DaoSysMinifierJs.minifyFolder(file, true);

                    new Resource(file, ".css").saveContent(css);
                    new Resource(file, ".js").saveContent(js);

                }else {
                    if (file.getName().endsWith(".css")) {
                        DaoSysMinifierCss.minifyFile(file);

                    } else if (file.getName().equals(".js")) {
                        DaoSysMinifierJs.minifyFile(file);
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

            if(new Resource(filePage, Properties.PROPERTIES_JSON).exists()) {
                final boolean isCached = new PageManager().cache(page);
                boolean isDeleted = true;
                if (isCached) {
                    final boolean isDeleted1 = new Resource(filePage, "content.jsp").delete();
                    final boolean isDeleted2 = new Resource(filePage, Properties.PROPERTIES_JSON).delete();
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

            final Resource fileConfEnv1 = new Resource(ResourceResolver.getRealPathWebInf("ui.config/application.json"));
            final Resource fileConfEnv2 = new Resource(UtilsResource.getResourcePath("application.json"));

            final ArrayList<Resource> listFileConfig = new ArrayList<>();
            listFileConfig.add(fileConfEnv1);
            listFileConfig.add(fileConfEnv2);

            for(final Resource fileConfEnv : listFileConfig) {
                if(fileConfEnv.exists() && fileConfEnv.isFile()) {
                    final Data data = new Data(fileConfEnv);
                    data.put("env", env);
                    isUpdated = fileConfEnv.saveContent(UtilsJson.prettyPrint(data.toJson()));
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
                    isUpdated = new Resource(UtilsResource.getResourcePath(resourceName + ".properties")).saveContent(data.toResourceBundleString());
                    n++;
                }
            }

        } catch (Exception e) {
            log.error("ERROR: AIONEM.net - JSP - WebApp : UpdateEnv :: {}", e.getMessage());
        }

        return isUpdated || n > 0;
    }

    public static Resource buildWar(final String warFileName) {

        final String webFileFolder = UtilsResource.getRealPathRoot();
        final Resource fileWebFolder = new Resource(webFileFolder);

        final String warFilePath = fileWebFolder.getParent() + "/" + (warFileName);
        final Resource fileWar = new Resource(warFilePath);

        final boolean isBuiltWar = buildWar(fileWebFolder, fileWar);

        return isBuiltWar ? fileWar : null;
    }

    public static boolean buildWar(final Resource fileWebFolder, final Resource fileWar) {

        boolean isBuilt = false;

        try {

            final boolean isEmpty;
            if(fileWar.exists()) {
                isEmpty = fileWar.delete();
            } else {
                isEmpty = true;
            }

            if(isEmpty) {

                final FileOutputStream fileOutputStream = fileWar.getFileOutputStream();
                final ZipOutputStream zipOutputStream = new ZipOutputStream(fileOutputStream);

                for (final Resource file : fileWebFolder.children()) {
                    createWar(file, file.getName(), zipOutputStream);
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

    public static void createWar(final Resource file, final String name, final ZipOutputStream zipOutputStream) throws IOException {

        if(file.isFolder()) {
            final String path = name + (!name.endsWith("/") ? "/" : "");
            zipOutputStream.putNextEntry(new ZipEntry(path));
            zipOutputStream.closeEntry();
            for (final Resource fileChild : file.children()) {
                createWar(fileChild, path + fileChild.getName(), zipOutputStream);
            }
            return;
        }

        final FileInputStream fileInputStream = file.getFileInputStream();
        final ZipEntry zipEntry = new ZipEntry(name);
        zipOutputStream.putNextEntry(zipEntry);

        final byte[] bytes = new byte[1024];
        int length;
        while((length = fileInputStream.read(bytes)) >= 0) {
            zipOutputStream.write(bytes, 0, length);
        }

        fileInputStream.close();
    }

    public static boolean uploadWar(final Resource fileWar) {

        boolean isUploaded = false;

        isUploaded = true;

        return isUploaded;
    }

}
