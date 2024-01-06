package aionem.net.sdk.web.modals;

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

    public String getName() {
        return file.getName();
    }

    public String getPath() {
        return UtilsResource.getRelativePath(path);
    }

    public String getRealPath() {
        return path;
    }

    public Resource getParent() {
        return new Resource(file.getParentFile());
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

    public boolean exists() {
        return file.exists();
    }

    public boolean isFile() {
        return file.isFile();
    }

    public boolean isFolder() {
        return file.isDirectory();
    }

    public boolean isPage() {
        return file.isDirectory();
    }

    public boolean isDrive() {
        return file.isDirectory();
    }

    public boolean isFrontend() {
        return file.isDirectory();
    }

    public boolean isConfig() {
        return file.isDirectory();
    }

    public boolean isApps() {
        return file.isDirectory();
    }

    public boolean isTemplate() {
        return file.isDirectory();
    }

    public boolean isEtc() {
        return file.isDirectory();
    }

    public boolean isSystem() {
        return file.isDirectory();
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
