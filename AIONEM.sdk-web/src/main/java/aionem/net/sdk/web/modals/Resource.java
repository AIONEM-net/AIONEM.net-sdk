package aionem.net.sdk.web.modals;

import aionem.net.sdk.web.AioWeb;
import lombok.Getter;

import java.io.File;


public class Resource {

    private final File file;
    @Getter
    private final String path;

    public Resource(final File file) {
        this.file = file;
        this.path = file.getAbsolutePath();
    }

    public Resource(final String path) {
        this.file = new File(path);
        this.path = path;
    }

    public String getName() {
        return file.getName();
    }

    public String getRealPath() {
        return file.getAbsolutePath();
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

}
