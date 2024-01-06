package aionem.net.sdk.web.utils;

import aionem.net.sdk.core.utils.UtilsText;
import aionem.net.sdk.data.utils.UtilsResource;
import lombok.extern.log4j.Log4j2;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;
import java.util.ResourceBundle;


@Log4j2
public class UtilsWeb {

    public static String readFileResource(final String fileName) {
        return readFile(fileName, "/WEB-INF/classes/", "/");
    }

    public static String readFileWebInf(final String fileName) {
        return readFile(fileName, "/WEB-INF/", "/");
    }

    public static String readFileConfig(final String fileName) {
        return readFile(fileName, "/WEB-INF/ui.config/", "/ui.config/", "/config/");
    }

    public static String readFileEnv(final String fileName) {
        return readFile(fileName, "/WEB-INF/ui.config/env/", "/ui.config/env/", "/config/env/");
    }

    public static String readFileI18n(final String fileName) {
        return readFile(fileName, "/WEB-INF/ui.config/i18n/", "/ui.config/i18n/", "/config/i18n/");
    }

    public static String readFileEtc(final String fileName) {
        return readFile(fileName, "/WEB-INF/ui.config/etc/", "/ui.config/etc/", "/etc/");
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

    public static String readFile(final String fileName, final String... folders) {
        final InputStream inputStream = readStream(fileName, folders);
        if(inputStream != null) {
            return UtilsText.toString(inputStream);
        }else  {
            return null;
        }
    }

    public static InputStream readStream(final String fileName, String... folders) {
        try {

            if(folders == null || folders.length == 0) folders = new String[]{""};

            for(final String folder : folders) {

                File file = new File(UtilsResource.getRealPathRoot(folder + fileName));
                if (file.exists() && file.isFile()) {
                    return new FileInputStream(file);
                }

                InputStream inputStream = UtilsResource.getResourceStreamOrParent(fileName, folder);
                if(inputStream != null) {
                    return inputStream;
                }
            }

        }catch(Exception e) {
            log.error("\nERROR: - readFile ::" + e + Arrays.toString(folders) + fileName +"\n");
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
