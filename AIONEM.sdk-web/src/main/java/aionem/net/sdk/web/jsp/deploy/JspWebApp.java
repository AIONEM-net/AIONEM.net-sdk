package aionem.net.sdk.web.jsp.deploy;

import aionem.net.sdk.data.Data;
import aionem.net.sdk.core.utils.UtilsText;
import aionem.net.sdk.data.utils.UtilsData;
import aionem.net.sdk.web.jsp.AioJsp;
import aionem.net.sdk.web.jsp.modals.JspPage;
import aionem.net.sdk.web.jsp.utils.JspUtils;
import lombok.extern.log4j.Log4j2;

import java.io.*;
import java.util.ArrayList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;


@Log4j2
public class JspWebApp {

    public static boolean deployWar(final AioJsp aioJsp, final String env, final String warFileName) {

        boolean isDeployedWar = false;

        final boolean isUpdatedEnv = updateEnv(aioJsp, env);

        if(isUpdatedEnv) {

            final ArrayList<File> listFilePagesCache = aioJsp.getListFilePagesAll("/en", "/it", "/rw", "/dev", "/auth/login", "/auth/register");

            final boolean isMinified = minify(aioJsp);

            final boolean isCachedAll = cacheAll(aioJsp, env, listFilePagesCache);

            if(isMinified && isCachedAll) {

                final File fileWar = buildWar(aioJsp, warFileName);
                final boolean isBuiltWar = fileWar != null && fileWar.exists();

                if(isBuiltWar) {

                    final boolean isUploadedWar = uploadWar(aioJsp, fileWar);

                    if(isUploadedWar) {
                        isDeployedWar = true;
                    }
                }

            }
        }

        return isDeployedWar;
    }

    public static boolean minify(final AioJsp aioJsp) {

        boolean isMinified = true;

        final File fileUiFrontend = new File(aioJsp.getRealPathRoot("/ui.frontend"));

        if(fileUiFrontend.isDirectory()) {

            for(final File file : fileUiFrontend.listFiles()) {

                if(file.isDirectory()) {

                    final String css = JspMinifierCss.minifyFolder(aioJsp, file, true);
                    final String js = JspMinifierJs.minifyFolder(aioJsp, file, true);

                    JspUtils.writeFile(new File(file, ".css"), css);
                    JspUtils.writeFile(new File(file, ".js"), js);

                }else {
                    if(file.getName().endsWith(".css")) {
                        JspMinifierCss.minifyFile(file);

                    }else if(file.getName().equals(".js")) {
                        JspMinifierJs.minifyFile(file);
                    }
                }

            }

        }

        return isMinified;
    }

    public static boolean cacheAll(final AioJsp aioJsp, String env, final ArrayList<File> listFilePagesAll) {

        boolean isCachedAll = true;

        final String rootPagePath = aioJsp.getRealPathRoot();

        for(int i = 0; i < listFilePagesAll.size(); i++) {
            final File filePage = listFilePagesAll.get(i);
            final String pagePath = filePage.getAbsolutePath().substring(rootPagePath.length() + 1);
            final JspPage jspPage = new JspPage(aioJsp, pagePath);

            if(new File(filePage, "index.jsp").exists()) {
                final boolean isCached = aioJsp.cache(env, jspPage);
                boolean isDeleted = false;
                if(isCached) {
                    isDeleted = new File(filePage, "properties.json").delete();
                    isDeleted = isDeleted || new File(filePage, "index.jsp").delete();
                }
                if(!isCached || !isDeleted) {
                    isCachedAll = false;
                    break;
                }
            }

        }

        return isCachedAll;
    }

    public static boolean updateEnv(final AioJsp aioJsp, final String env) {

        boolean isUpdated = false;

        try {

            final File fileWebXml = new File(aioJsp.getRealPathWebInf("web.xml"));
            final String webXml = UtilsText.toString(fileWebXml, true);

            final String webXmlNew = UtilsData.replaceVariables(webXml, new Data()
                    .put("env", env)
            );

            isUpdated = JspUtils.writeFile(fileWebXml, webXmlNew);

        } catch (Exception e) {
            log.error("ERROR: AIONEM.net - JSP - WebApp : UpdateEnv :: {}", e.getMessage());
        }

        return isUpdated;
    }

    public static File buildWar(final AioJsp aioJsp, final String warFileName) {

        final String webFileFolder = aioJsp.getRealPathRoot();
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

                for (final File file : fileWebFolder.listFiles()) {
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

    public static void createWar(final File file, final String name, final ZipOutputStream zipOutputStream) throws IOException {

        if(file.isDirectory()) {
            final String path = name + (!name.endsWith("/") ? "/" : "");
            zipOutputStream.putNextEntry(new ZipEntry(path));
            zipOutputStream.closeEntry();
            final File[] children = file.listFiles();
            for (final File fileChild : children) {
                createWar(fileChild, path + fileChild.getName(), zipOutputStream);
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

    public static boolean uploadWar(final AioJsp aioJsp, final File fileWar) {

        boolean isUploaded = false;

        isUploaded = true;

        return isUploaded;
    }

}
