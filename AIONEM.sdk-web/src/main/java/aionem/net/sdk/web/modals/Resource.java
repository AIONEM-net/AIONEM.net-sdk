package aionem.net.sdk.web.modals;

import aionem.net.sdk.core.utils.UtilsText;
import aionem.net.sdk.data.utils.UtilsResource;
import lombok.Getter;

import java.io.File;
import java.util.ArrayList;
import java.util.Objects;


@Getter
public class Resource {

    private final File file;
    private final String path;

    public Resource(final File file) {
        this.file = file;
        this.path = file.getAbsolutePath();
    }

    public Resource(final String path) {
        this.path = path;
        this.file = new File(path);
    }

    public Resource(final Resource resource, final String child) {
        this(resource.child(child).file);
    }

    public String getName() {
        return file.getName();
    }

    public String getPath() {
        return UtilsResource.getRelativePath(path);
    }

    public String getRealPath() {
        return path;
    }

    public Resource child(final String name) {
        return new Resource(new File(file, name));
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
        return hasParent() ? new Resource(file.getParentFile()) : null;
    }

    public boolean hasParent() {
        return !isRootPath();
    }

    public boolean delete() {
        return file.delete();
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
        final String path = getPath();
        return UtilsText.isEmpty(path) || path.equals("/");
    }

    public boolean isPage() {
        return getPath().startsWith("/ui.page");
    }

    public boolean isDrive() {
        return getPath().startsWith("/ui.drive");
    }

    public boolean isFrontend() {
        return getPath().startsWith("/ui.frontend");
    }

    public boolean isConfig() {
        return getPath().startsWith("/WEB-INF/ui.config");
    }

    public boolean isApps() {
        return getPath().startsWith("/WEB-INF/ui.apps");
    }

    public boolean isTemplate() {
        return getPath().startsWith("/WEB-INF/ui.template");
    }

    public boolean isEtc() {
        return getPath().startsWith("/WEB-INF/ui.config/etc");
    }

    public boolean isSystem() {
        return getPath().startsWith("/ui.system");
    }

    @Override
    public String toString() {
        return path;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        Resource resource = (Resource) object;
        return Objects.equals(file, resource.file) && Objects.equals(path, resource.path);
    }

    @Override
    public int hashCode() {
        return Objects.hash(file, path);
    }

}
