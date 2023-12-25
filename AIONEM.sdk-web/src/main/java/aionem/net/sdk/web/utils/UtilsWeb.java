package aionem.net.sdk.web.utils;

import aionem.net.sdk.core.utils.UtilsText;
import aionem.net.sdk.data.utils.UtilsResource;
import aionem.net.sdk.web.AioWeb;
import lombok.extern.log4j.Log4j2;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Locale;
import java.util.ResourceBundle;


@Log4j2
public class UtilsWeb {

    public static String readFileResource(final AioWeb aioWeb, final String fileName) {
        return readFile(aioWeb, "/WEB-INF/classes/", "/", fileName);
    }

    public static String readFileWebInf(final AioWeb aioWeb, final String fileName) {
        return readFile(aioWeb, "/WEB-INF/", "/", fileName);
    }

    public static String readFileConfig(final AioWeb aioWeb, final String fileName) {
        return readFile(aioWeb, "/WEB-INF/ui.config/", "/config/", fileName);
    }

    public static String readFileEnv(final AioWeb aioWeb, final String fileName) {
        return readFile(aioWeb, "/WEB-INF/ui.config/env/", "/config/env/", fileName);
    }

    public static String readFileI18n(final AioWeb aioWeb, final String fileName) {
        return readFile(aioWeb, "/WEB-INF/ui.config/i18n/", "/config/i18n/", fileName);
    }

    public static String readFileEtc(final AioWeb aioWeb, final String fileName) {
        return readFile(aioWeb, "/WEB-INF/ui.app/etc/", "/etc/", fileName);
    }

    public static ResourceBundle getResourceBundleConfig(final String fileName) {
        return UtilsResource.getResourceBundle("/config/" + fileName);
    }

    public static ResourceBundle getResourceBundleEnv(final String fileName) {
        return UtilsResource.getResourceBundle("/config/env/" + fileName);
    }

    public static ResourceBundle getResourceBundleI18n(final String fileName) {
        return UtilsResource.getResourceBundle("/config/i18n/" + fileName);
    }

    public static ResourceBundle getResourceBundleI18n(final String fileName, final Locale locale) {
        return UtilsResource.getResourceBundle("/config/i18n/" + fileName, locale);
    }

    public static ResourceBundle getResourceBundleEtc(final String fileName) {
        return UtilsResource.getResourceBundle("/etc/" + fileName);
    }

    public static String readFile(final AioWeb aioWeb, final String folder1, final String folder2, final String fileName) {
        final InputStream inputStream = readStream(aioWeb, folder1, folder2, fileName);
        if(inputStream != null) {
            return UtilsText.toString(inputStream);
        }else  {
            return null;
        }
    }

    public static InputStream readStream(AioWeb aioWeb, final String folder1, final String folder2, final String fileName) {
        try {
            if(aioWeb != null) {
                final File file = new File(aioWeb.getRealPathRoot(folder1 + fileName));
                if(file.exists() && !file.isDirectory()) {
                    return new FileInputStream(file);
                }
            }

            if(aioWeb == null) aioWeb = new AioWeb();
            InputStream inputStream = aioWeb.getResourceAsStream(folder1 + fileName);
            if(inputStream != null) {
                return inputStream;
            }else {
                inputStream = aioWeb.getResourceAsStream(folder2 + fileName);
                if(inputStream != null) {
                    return inputStream;
                }
            }

        }catch(Exception e) {
            log.error("\nERROR: - readFile ::" + e + folder1 + fileName +"\n");
        }
        return null;
    }

    public static boolean writeFile(final String realPath, final String contents) {
        return writeFile(new File(realPath), contents);
    }

    public static boolean writeFile(final File file, final String contents) {
        boolean isWritten = false;
        try(final FileWriter fileWriter = new FileWriter(file, StandardCharsets.UTF_8)) {
            fileWriter.write(contents);
            isWritten = true;
        } catch (Exception e) {
            log.error("\nERROR: - writeFile ::"+ e +"\n");
        }
        return isWritten;
    }

    public static ArrayList<File> findFiles(final File fileFolder, final FilenameFilter filenameFilter) {

        final ArrayList<File> listFiles = new ArrayList<>();

        if(fileFolder.isDirectory()) {
            final File[] files = fileFolder.listFiles();
            if(files != null) {
                for(final File file : files) {
                    if(file.isDirectory()) {
                        listFiles.addAll(findFiles(file, filenameFilter));
                    } else {
                        if(filenameFilter.accept(fileFolder, file.getName())) {
                            listFiles.add(file);
                        }
                    }
                }
            }
        }

        return listFiles;
    }

}
