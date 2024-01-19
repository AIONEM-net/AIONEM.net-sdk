package aionem.net.sdk.web.beans;

import aionem.net.sdk.core.utils.UtilsText;
import aionem.net.sdk.data.beans.Data;
import aionem.net.sdk.data.utils.UtilsResource;
import aionem.net.sdk.web.dao.ResourceResolver;
import aionem.net.sdk.web.utils.UtilsDrive;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Objects;


@Getter
@Log4j2
public class Resource {

    private final File file;
    private final String pathReal;
    private final String pathRelative;
    private final String pathSystem;

    public Resource(final File file) {
        this.file = file;
        this.pathReal = file.getAbsolutePath();
        this.pathRelative = UtilsResource.getRelativePath(pathReal);

        final String system;
        if(isPage()) {
            system = pathRelative.substring("/ui.page".length());
        }else if(isDrive()) {
            system = pathRelative.substring("/ui.drive".length());
        }else if(isFrontend()) {
            system = pathRelative.substring("/ui.frontend".length());
        }else if(isApps()) {
            system = pathRelative.substring("/WEB-INF/ui.apps".length());
        }else if(isConf()) {
            system = pathRelative.substring("/WEB-INF/ui.config/conf".length());
        }else if(isI18n()) {
            system = pathRelative.substring("/WEB-INF/ui.config/i18n".length());
        }else if(isEtc()) {
            system = pathRelative.substring("/WEB-INF/ui.config/etc".length());
        }else if(isTemplate()) {
            system = pathRelative.substring("/WEB-INF/ui.template".length());
        }else if(isSystem()) {
            system = pathRelative.substring("/ui.system".length());
        }else {
            system = this.pathRelative;
        }
        this.pathSystem = system.startsWith("/") ? system.substring("/".length()) : system;
    }

    public Resource(final String pathReal) {
        this(new File(ResourceResolver.isSystemPathRoot(pathReal) ? UtilsResource.getRealPathRoot(pathReal) : pathReal));
    }

    public Resource(final String... paths) {
        this(UtilsResource.path(paths));
    }

    public Resource(final Path filePath) {
        this(filePath.toFile());
    }

    public Resource(final Resource resource, final String child) {
        this(resource.child(child).file);
    }

    public String getName() {
        return file.getName();
    }

    public String getRealPath() {
        return pathReal;
    }

    public String getRelativePath() {
        return pathRelative;
    }

    public String getSystemPath() {
        return pathSystem;
    }

    public long getSize() {
        return file.length();
    }

    public Resource child(final String name) {
        return new Resource(UtilsResource.path(pathReal, name));
    }

    public Resource child(final String... pathsName) {
        return new Resource(UtilsResource.path(pathsName));
    }

    public ArrayList<Resource> children() {
        final ArrayList<Resource> children = new ArrayList<>();
        final File[] files = file.listFiles();
        if(files != null) {
            for(File file : files) {
                children.add(new Resource(file));
            }
        }
        return children;
    }

    public Resource getParent() {
        return hasParent() ? new Resource(UtilsResource.parentFolder(pathReal)) : null;
    }

    public boolean hasParent() {
        return !isRootPath();
    }

    public Properties getProperties() {
        return new Properties(child(Properties.PROPERTIES_JSON));
    }

    public String readContent() {
        return readContent(true);
    }

    public String readContent(final boolean isLine) {
        if(exists() && isFile()) {
            return UtilsText.toString(file, isLine);
        }
        return null;
    }

    public boolean saveContent(final String contents) {
        boolean isWritten = false;
        if(exists() && isFile()) {
            try (final FileWriter fileWriter = new FileWriter(file, StandardCharsets.UTF_8)) {
                fileWriter.write(contents);
                isWritten = true;
            } catch (final Exception e) {
                log.error("\nERROR: - writeFile ::" + e + "\n");
            }
        }
        return isWritten;
    }

    public InputStream getInputStream() {
        return getFileInputStream();
    }

    public OutputStream getOutputStream() {
        return getFileOutputStream();
    }

    public FileInputStream getFileInputStream() {
        try {
            return new FileInputStream(file);
        } catch (final FileNotFoundException e) {
            log.error("\nError: file does not exist");
            return null;
        }
    }

    public FileOutputStream getFileOutputStream() {
        try {
            return new FileOutputStream(file);
        } catch (final FileNotFoundException e) {
            log.error("\nError: file does not exist");
            return null;
        }
    }

    public String getExtension() {
        return UtilsDrive.getFileExtension(getName());
    }

    public boolean delete() {
        boolean isDeleted = false;
        if(exists()) {
            if(isPage() || isDrive()) {
                isDeleted = file.delete();
            }
        }
        return isDeleted;
    }

    public boolean exists() {
        return file.exists();
    }

    public boolean isFile() {
        return file.isFile();
    }

    public boolean isFolder() {
        return file.isDirectory();
    }

    public boolean isRootPath() {
        return UtilsText.isEmpty(pathRelative) || pathRelative.equals("/");
    }

    public boolean isPage() {
        return pathRelative.startsWith("/ui.page");
    }

    public boolean isDrive() {
        return pathRelative.startsWith("/ui.drive");
    }

    public boolean isFrontend() {
        return pathRelative.startsWith("/ui.frontend");
    }

    public boolean isApps() {
        return pathRelative.startsWith("/WEB-INF/ui.apps");
    }

    public boolean isTemplate() {
        return pathRelative.startsWith("/WEB-INF/ui.template");
    }

    public boolean isConf() {
        return pathRelative.startsWith("/WEB-INF/ui.config/conf");
    }

    public boolean isI18n() {
        return pathRelative.startsWith("/WEB-INF/ui.config/i18n");
    }

    public boolean isEtc() {
        return pathRelative.startsWith("/WEB-INF/ui.config/etc");
    }

    public boolean isSystem() {
        return pathRelative.startsWith("/ui.system");
    }

    public String getType() {
        final String type;
        if(isPage()) {
            type = "page";
        }else if(isDrive()) {
            type = "drive";
        }else if(isFrontend()) {
            type = "frontend";
        }else if(isApps()) {
            type = "apps";
        }else if(isConf()) {
            type = "env";
        }else if(isI18n()) {
            type = "i18n";
        }else if(isEtc()) {
            type = "etc";
        }else if(isTemplate()) {
            type = "template";
        }else if(isSystem()) {
            type = "system";
        }else {
            type = "";
        }
        return type;
    }

    public Data toData() {
        final Data data = getProperties().toData();
        data.put("name", getName());
        data.put("pathRelative", pathRelative);
        data.put("pathSystem", pathSystem);
        data.put("isFolder", isFolder());
        data.put("isFile", isFile());
        data.put("size", getSize());
        data.put("type", getType());
        return data;
    }

    @Override
    public String toString() {
        return pathReal;
    }

    @Override
    public boolean equals(final Object object) {
        if(this == object) return true;
        if(object == null || getClass() != object.getClass()) return false;
        final Resource resource = (Resource) object;
        return Objects.equals(file, resource.file) && Objects.equals(pathReal, resource.pathReal);
    }

    @Override
    public int hashCode() {
        return Objects.hash(file, pathReal);
    }

}
