package aionem.net.sdk.jsp;

import aionem.net.sdk.core.data.AlnData;
import aionem.net.sdk.core.utils.AlnUtilsText;
import lombok.extern.log4j.Log4j2;

import java.io.*;
import java.util.ArrayList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;


@Log4j2
public class AlnJspWebApp {

    public static boolean deployWar(final AlnJsp alnJsp, final String env, final String warFileName) {

        boolean isDeployedWar = false;

        final boolean isUpdatedEnv = updateEnv(alnJsp, env);

        if(isUpdatedEnv) {

            final ArrayList<File> listFilePagesCache = alnJsp.getListFilePagesAll("/en", "/it", "/rw", "/dev", "/auth/login", "/auth/register");

            final boolean isMinified = minify(alnJsp);

            final boolean isCachedAll = cacheAll(alnJsp, env, listFilePagesCache);

            if(isMinified && isCachedAll) {

                final File fileWar = buildWar(alnJsp, warFileName);
                final boolean isBuiltWar = fileWar != null && fileWar.exists();

                if(isBuiltWar) {

                    final boolean isUploadedWar = uploadWar(alnJsp, fileWar);

                    if(isUploadedWar) {
                        isDeployedWar = true;
                    }
                }

            }
        }

        return isDeployedWar;
    }

    public static boolean minify(final AlnJsp alnJsp) {

        boolean isMinified = true;

        final File fileUiFrontend = new File(alnJsp.getRealPathRoot("/ui.frontend"));

        if(fileUiFrontend.isDirectory()) {

            for(final File file : fileUiFrontend.listFiles()) {

                if(file.isDirectory()) {

                    final String css = AlnJspMinifierCss.minifyFolder(alnJsp, file, true);
                    final String js = AlnJspMinifierJs.minifyFolder(alnJsp, file, true);

                    AlnJspUtils.writeFile(new File(file, ".css"), css);
                    AlnJspUtils.writeFile(new File(file, ".js"), js);

                }else {
                    if(file.getName().endsWith(".css")) {
                        AlnJspMinifierCss.minifyFile(file);

                    }else if(file.getName().equals(".js")) {
                        AlnJspMinifierJs.minifyFile(file);
                    }
                }

            }

        }

        return isMinified;
    }

    public static boolean cacheAll(final AlnJsp alnJsp, String env, final ArrayList<File> listFilePagesAll) {

        boolean isCachedAll = true;

        final String rootPagePath = alnJsp.getRealPathRoot();

        for(int i = 0; i < listFilePagesAll.size(); i++) {
            final File filePage = listFilePagesAll.get(i);
            final String pagePath = filePage.getAbsolutePath().substring(rootPagePath.length() + 1);
            final AlnJspPage jspPage = new AlnJspPage(alnJsp, pagePath);

            if(new File(filePage, "index.jsp").exists()) {
                final boolean isCached = alnJsp.cache(env, jspPage);
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

    public static boolean updateEnv(final AlnJsp alnJsp, final String env) {

        boolean isUpdated = false;

        try {

            final File fileWebXml = new File(alnJsp.getRealPathWebInf("web.xml"));
            final String webXml = AlnUtilsText.toString(fileWebXml, true);

            final String webXmlNew = AlnUtilsText.replaceVariables(webXml, new AlnData()
                    .put("env", env)
            );

            isUpdated = AlnJspUtils.writeFile(fileWebXml, webXmlNew);

        } catch (Exception e) {
            log.error("ERROR: AIONEM.net - JSP - WebApp : UpdateEnv :: {}", e.getMessage());
        }

        return isUpdated;
    }

    public static File buildWar(final AlnJsp alnJsp, final String warFileName) {

        final String webFileFolder = alnJsp.getRealPathRoot();
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

    public static boolean uploadWar(final AlnJsp alnJsp, final File fileWar) {

        boolean isUploaded = false;

        isUploaded = true;

        return isUploaded;
    }

}
