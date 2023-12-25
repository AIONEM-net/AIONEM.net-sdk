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

    public static <T> ClassLoader getClassLoader(final Class<T> tClass) {
        return tClass.getClassLoader();
    }

    public static <T> URL getResource(final Class<T> tClass, final String name) {
        return getClassLoader(tClass).getResource(name);
    }

    public static <T> InputStream getResourceAsStream(final Class<T> tClass, final String name) {
        final URL url = getResource(tClass, name);
        try {
            if (url != null) return url.openStream();
        }catch (IOException ignore) {
        }
        return null;
    }

    public static <T> InputStream getParentResourceAsStream(final Class<T> tClass, final String name, String... folders) {
        if(folders == null || folders.length == 0) folders = new String[]{""};
        for(final String folder : folders) {
            try {
                final File file = getResourceParent(tClass, folder +"/"+  name);
                if(file != null && file.exists() && !file.isDirectory()) {
                    return new FileInputStream(file);
                }else {
                    return getResourceAsStream(tClass, folder +"/"+  name);
                }

            }catch (IOException ignore) {
            }
        }
        return null;
    }

    public static <T> String readParentResource(final Class<T> tClass, final String name, String... folders) {
        return UtilsText.toString(getParentResourceAsStream(tClass, name, folders));
    }

    public static <T> File getResourceFile(final Class<T> tClass, final String name) {
        final URL resource = getResource(tClass, name);
        return resource != null ? new File(resource.getFile()) : null;
    }

    public static <T> File getResourceParent(final Class<T> tClass) {
        final File file = getResourceFile(tClass, "");
        return file != null ? file.getParentFile() : null;
    }

    public static <T> File getResourceParent(final Class<T> tClass, final String name) {
        final File file = getResourceParent(tClass);
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
                    return ResourceBundle.getBundle(folder +"/"+  name, locale);
                } else {
                    return ResourceBundle.getBundle(folder +"/"+  name);
                }
            } catch (Exception ignore) {
            }
        }
        return null;
    }

}
