package aionem.net.sdk.data.utils;

import aionem.net.sdk.core.utils.UtilsText;
import lombok.extern.log4j.Log4j2;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.Locale;
import java.util.ResourceBundle;


@Log4j2
public class UtilsResource {

    private static String ROOT_PATH = "";
    private static String RESOURCE_PATH = "";
    private static String PARENT_PATH = "";

    public static String getRealPathRoot() {
        return getRealPathRoot("");
    }

    public static String getRealPathRoot(final String path) {
        if(UtilsText.isEmpty(ROOT_PATH)) {
            ROOT_PATH = parentFolder(getRealPathParent());
        }
        return path(ROOT_PATH, path);
    }

    public static String getRealPathParent() {
        return getRealPathParent("");
    }

    public static String getRealPathParent(final String path) {
        if(UtilsText.isEmpty(PARENT_PATH)) {
            PARENT_PATH = parentFolder(getResourcePath());
        }
        return path(PARENT_PATH, path);
    }

    public static String getResourcePath() {
        return getResourcePath("");
    }

    public static String getResourcePath(final String path) {
        if(UtilsText.isEmpty(RESOURCE_PATH)) {
            final URL resource = UtilsResource.class.getClassLoader().getResource("");
            RESOURCE_PATH = path(resource != null ? resource.getFile() : "");
        }
        return path(RESOURCE_PATH, path);
    }

    public static String getRelativePath(String path) {
        final String realPathRoot = getRealPathRoot();
        if(path.startsWith(realPathRoot)) {
            path = path.substring(realPathRoot.length());
            if(!path.startsWith("/")) path = "/" + path;
        }
        return path;
    }

    public static InputStream getResourceStream(final String name, String... folders) {
        if(folders == null || folders.length == 0) folders = new String[]{""};
        for(final String folder : folders) {
            try {
                final URL resource = UtilsResource.class.getClassLoader().getResource(path(folder, name));
                if(resource != null) {
                    return resource.openStream();
                }

                final File file = getResourceFile(path(folder, name));
                if(file.exists() && file.isFile()) {
                    return new FileInputStream(file);
                }
            }catch (Exception ignore) {
            }
        }
        return null;
    }

    public static InputStream getResourceStreamOrParent(final String name, String... folders) {
        if(folders == null || folders.length == 0) folders = new String[]{""};
        for(final String folder : folders) {
            try {
                final File file = getRealFileParent(path(folder, name));
                if(file.exists() && file.isFile()) {
                    return new FileInputStream(file);
                }else {
                    final InputStream inputStream = getResourceStream(path(folder, name));
                    if(inputStream != null) {
                        return inputStream;
                    }
                }
            }catch (Exception ignore) {
            }
        }
        return null;
    }

    public static String readResource(final String name, String... folders) {
        return UtilsText.toString(getResourceStream(name, folders));
    }

    public static String readResourceOrParent(final String name, String... folders) {
        return UtilsText.toString(getResourceStreamOrParent(name, folders));
    }

    public static ResourceBundle getResourceBundle(final String name, String... folders) {
        return getResourceBundle(name, null, folders);
    }

    public static ResourceBundle getResourceBundle(final String name, final Locale locale, String... folders) {
        ResourceBundle resourceBundle;
        if(folders == null || folders.length == 0) folders = new String[]{""};
        for(final String folder : folders) {
            try {
                if(locale != null) {
                    resourceBundle = ResourceBundle.getBundle(path(folder, name), locale);
                }else {
                    resourceBundle = ResourceBundle.getBundle(path(folder, name));
                }
                if(resourceBundle != null) return resourceBundle;
            }catch(final Exception ignore) {
            }
        }
        return null;
    }

    public static String path(final String... arrayPaths) {
        String paths = "";
        if(arrayPaths != null && arrayPaths.length > 0) {
            for(int i = 0; i < arrayPaths.length; i++) {
                String path = arrayPaths[i];
                if(i == 0) {
                    paths = path;
                }else {
                    paths = path(paths, path);
                }
            }
        }
        return paths;
    }

    public static String path(final String folder, final String name) {
        String path;
        if(UtilsText.isEmpty(folder) && UtilsText.isEmpty(name)) return "";

        if(UtilsText.isEmpty(folder)) {
            path = name;
        }else if(UtilsText.isEmpty(name)) {
            path = folder;
        }else {
            path = folder +(!folder.endsWith("/") && !name.startsWith("/") ? "/" : "")+  name;
        }

        if(path.endsWith("/")) {
            path = path.substring(0, path.length() -1);
        }
        return path
                .replace("\\", "/")
                .replace("//", "/");
    }

    public static String parentFolder(final String folder) {
        if(UtilsText.isEmpty(folder)) return folder;
        return folder.contains("/") ? path(folder.substring(0, folder.lastIndexOf("/"))) : "";
    }

    private static File getResourceFile(final String path) {
        return new File(getResourcePath(path));
    }

    private static File getRealFileParent(final String path) {
        return new File(getRealPathParent(path));
    }

    public static File getRealFileRoot(final String path) {
        return new File(getRealPathRoot(path));
    }

}
