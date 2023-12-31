package aionem.net.sdk.data.utils;

import aionem.net.sdk.core.utils.UtilsText;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Locale;
import java.util.ResourceBundle;

public class UtilsResource {

    public static <T> ClassLoader getClassLoader() {
        return UtilsResource.class.getClassLoader();
    }

    public static <T> URL getResource(final String name) {
        return getClassLoader().getResource(name);
    }

    public static <T> InputStream getResourceAsStream(final String name, String... folders) {
        if(folders == null || folders.length == 0) folders = new String[]{""};
        for(final String folder : folders) {
            try {
                final URL url = getResource(folder +(!folder.endsWith("/") && !name.startsWith("/") ? "/" : "")+  name);
                if (url != null) return url.openStream();
            }catch (IOException ignore) {
            }
        }
        return null;
    }

    public static <T> InputStream getParentResourceAsStream(final String name, String... folders) {
        if(folders == null || folders.length == 0) folders = new String[]{""};
        for(final String folder : folders) {
            try {
                final File file = getResourceParent(folder +(!folder.endsWith("/") && !name.startsWith("/") ? "/" : "")+  name);
                if(file != null && file.exists() && file.isFile()) {
                    return new FileInputStream(file);
                }else {
                    InputStream inputStream = getResourceAsStream(folder +(!folder.endsWith("/") && !name.startsWith("/") ? "/" : "")+  name);
                    if(inputStream != null) {
                        return inputStream;
                    }
                }
            }catch (IOException ignore) {
            }
        }
        return null;
    }

    public static <T> String readResource(final String name, String... folders) {
        return UtilsText.toString(getResourceAsStream(name, folders));
    }

    public static <T> String readParentResource(final String name, String... folders) {
        return UtilsText.toString(getParentResourceAsStream(name, folders));
    }

    public static <T> File getResourceFile(final String name) {
        final URL resource = getResource(name);
        return resource != null ? new File(resource.getFile()) : null;
    }

    public static <T> File getResourceParent() {
        final File file = getResourceFile("");
        return file != null ? file.getParentFile() : null;
    }

    public static <T> File getResourceParent(final String name) {
        final File file = getResourceParent();
        return file != null ? new File(file, name) : null;
    }

    public static ResourceBundle getResourceBundle(final String name, String... folders) {
        return getResourceBundle(name, null, folders);
    }

    public static ResourceBundle getResourceBundle(final String name, final Locale locale, String... folders) {
        if(folders == null || folders.length == 0) folders = new String[]{""};
        for(final String folder : folders) {
            try {
                if (locale != null) {
                    return ResourceBundle.getBundle(folder +(!folder.endsWith("/") && !name.startsWith("/") ? "/" : "")+  name, locale);
                } else {
                    return ResourceBundle.getBundle(folder +(!folder.endsWith("/") && !name.startsWith("/") ? "/" : "")+  name);
                }
            } catch (Exception ignore) {
            }
        }
        return null;
    }

}
