package aionem.net.sdk.web.utils;

import aionem.net.sdk.core.utils.UtilsText;
import aionem.net.sdk.data.utils.UtilsResource;
import aionem.net.sdk.web.beans.Resource;
import lombok.extern.log4j.Log4j2;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;
import java.util.ResourceBundle;


@Log4j2
public class UtilsWeb {

    public static String readResource(final String name) {
        return readResource(name, "/WEB-INF/classes/", "/");
    }

    public static String readResourceWebInf(final String name) {
        return readResource(name, "/WEB-INF/", "/");
    }

    public static String readResourceConfig(final String name) {
        return readResource(name, "/WEB-INF/ui.config/", "/ui.config/", "/config/");
    }

    public static String readResourceEnv(final String name) {
        return readResource(name, "/WEB-INF/ui.config/env/", "/ui.config/env/", "/config/env/");
    }

    public static String readResourceI18n(final String name) {
        return readResource(name, "/WEB-INF/ui.config/i18n/", "/ui.config/i18n/", "/config/i18n/");
    }

    public static String readResourceEtc(final String name) {
        return readResource(name, "/WEB-INF/ui.config/etc/", "/ui.config/etc/", "/etc/");
    }

    public static ResourceBundle getResourceBundleConfig(final String name) {
        return UtilsResource.getResourceBundle("/config/" + name);
    }

    public static ResourceBundle getResourceBundleEnv(final String name) {
        return UtilsResource.getResourceBundle("/config/env/" + name);
    }

    public static ResourceBundle getResourceBundleI18n(final String name) {
        return UtilsResource.getResourceBundle("/config/i18n/" + name);
    }

    public static ResourceBundle getResourceBundleI18n(final String name, final Locale locale) {
        return UtilsResource.getResourceBundle("/config/i18n/" + name, locale);
    }

    public static ResourceBundle getResourceBundleEtc(final String name) {
        return UtilsResource.getResourceBundle("/etc/" + name);
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
                if (resource.exists() && resource.isFile()) {
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

    public static boolean writeResource(final String realPath, final String contents) {
        return writeResource(new Resource(realPath), contents);
    }

    public static boolean writeResource(final Resource resource, final String contents) {
        boolean isWritten = false;
        try(final FileWriter fileWriter = new FileWriter(resource.getFile(), StandardCharsets.UTF_8)) {
            fileWriter.write(contents);
            isWritten = true;
        } catch (Exception e) {
            log.error("\nERROR: - writeFile ::"+ e +"\n");
        }
        return isWritten;
    }

    public static ArrayList<Resource> findResources(final Resource resourceFolder, final FilenameFilter filenameFilter) {

        final ArrayList<Resource> listResources = new ArrayList<>();

        if(resourceFolder.isFolder()) {
            for(final Resource resource : resourceFolder.children()) {
                if(resource.isFolder()) {
                    listResources.addAll(findResources(resource, filenameFilter));
                } else {
                    if(filenameFilter.accept(resourceFolder.getFile(), resource.getName())) {
                        listResources.add(resource);
                    }
                }
            }
        }

        return listResources;
    }

}
