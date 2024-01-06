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

    private static String ROOT_PATH = "";

    public static String getRealPathRoot() {
        return getRealPathRoot("");
    }

    public static String getRealPathRoot(final String path) {
        if(UtilsText.isEmpty(ROOT_PATH)) {
            ROOT_PATH = getResourceFolderRoot().getPath();
        }
        return ROOT_PATH + (!UtilsText.isEmpty(path) ? "/" : "") + path;
    }

    public static String getRealPathParent() {
        return getRealPathParent("");
    }

    public static String getRealPathParent(final String path) {
        return getResourceFileParent(path).getPath();
    }

    public static String getRelativePath(String path) {
        final String realPathRoot = getRealPathRoot();
        if(!path.startsWith("/")) path = "/" + path;
        if(path.startsWith(realPathRoot)) {
            path = path.substring(realPathRoot.length());
        }
        return path;
    }

    public static <T> InputStream getResourceStream(final String name, String... folders) {
        if(folders == null || folders.length == 0) folders = new String[]{""};
        for(final String folder : folders) {
            try {
                final File file = getResourceFile(folder +(!folder.endsWith("/") && !name.startsWith("/") ? "/" : "")+  name);
                if(file.exists() && file.isFile()) {
                    return new FileInputStream(file);
                }
            }catch (IOException ignore) {
            }
        }
        return null;
    }

    public static <T> InputStream getResourceStreamOrParent(final String name, String... folders) {
        if(folders == null || folders.length == 0) folders = new String[]{""};
        for(final String folder : folders) {
            try {
                final File file = getResourceFileParent(folder +(!folder.endsWith("/") && !name.startsWith("/") ? "/" : "")+  name);
                if(file.exists() && file.isFile()) {
                    return new FileInputStream(file);
                }else {
                    InputStream inputStream = getResourceStream(folder +(!folder.endsWith("/") && !name.startsWith("/") ? "/" : "")+  name);
                    if(inputStream != null) {
                        return inputStream;
                    }
                }
            }catch (IOException ignore) {
            }
        }
        return null;
    }

    public static <T> InputStream getResourceStreamOrRoot(final String name, String... folders) {
        if(folders == null || folders.length == 0) folders = new String[]{""};
        for(final String folder : folders) {
            try {
                final File file = getResourceFileParent(folder +(!folder.endsWith("/") && !name.startsWith("/") ? "/" : "")+  name);
                if(file.exists() && file.isFile()) {
                    return new FileInputStream(file);
                }else {
                    InputStream inputStream = getResourceStreamOrParent(folder +(!folder.endsWith("/") && !name.startsWith("/") ? "/" : "")+  name);
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
        return UtilsText.toString(getResourceStream(name, folders));
    }

    public static <T> String readResourceOrParent(final String name, String... folders) {
        return UtilsText.toString(getResourceStreamOrParent(name, folders));
    }

    public static <T> String readResourceOrRoot(final String name, String... folders) {
        return UtilsText.toString(getResourceStreamOrParent(name, folders));
    }

    public static File getResourceFolder() {
        return getResourceFile("");
    }

    public static File getResourceFile(final String name) {
        final URL resource = UtilsResource.class.getClassLoader().getResource(name);
        return resource != null ? new File(resource.getFile()) : new File(name);
    }

    public static File getResourceFolderParent() {
        return getResourceFolder().getParentFile();
    }

    public static <T> File getResourceFileParent(final String name) {
        return new File(getResourceFolderParent(), name);
    }

    public static <T> File getResourceFolderRoot() {
        return getResourceFolderParent().getParentFile();
    }

    public static <T> File getResourceFileRoot(final String name) {
        return new File(getResourceFolderRoot(), name);
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

    public static File getRealFileRoot(final String path) {
        return new File(getRealPathRoot(path));
    }

}
