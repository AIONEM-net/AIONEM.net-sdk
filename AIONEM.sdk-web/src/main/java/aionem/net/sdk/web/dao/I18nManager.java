package aionem.net.sdk.web.dao;

import aionem.net.sdk.web.AioWeb;
import aionem.net.sdk.web.utils.UtilsWeb;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;


public class I18nManager {

    private final AioWeb aioWeb;

    public I18nManager(final AioWeb aioWeb) {
        this.aioWeb = aioWeb;
    }

    public ArrayList<File> getListFoldersI18n() {

        final ArrayList<File> listI18nFolders = new ArrayList<>();

        final File folder1 = aioWeb.getRealFileWebInf("/ui.config/i18n");
        final File folder2 = aioWeb.getResourceFile("/config/i18n");
        final File folder3 = aioWeb.getResourceFile("/i18n");

        if(folder1 != null && folder1.exists() && folder1.isDirectory()) listI18nFolders.add(folder1);
        if(folder3 != null && folder2.exists() && folder2.isDirectory()) listI18nFolders.add(folder2);
        if(folder3 != null && folder3.exists() && folder3.isDirectory()) listI18nFolders.add(folder3);

        return listI18nFolders;
    }

    public ArrayList<File> getListI18nFiles() {

        final ArrayList<File> filesI18n = new ArrayList<>();

        filesI18n.addAll(getListI18nFilesJson());

        filesI18n.addAll(getListI18nFilesProperties());

        return filesI18n;
    }

    public ArrayList<File> getListI18nFilesJson() {

        final ArrayList<File> filesI18n = new ArrayList<>();

        final FilenameFilter filterJson = new FilenameFilter() {
            @Override
            public boolean accept(final File file, final String name) {
                return name.toLowerCase().endsWith(".json");
            }
        };

        for(final File folderI18n : getListFoldersI18n()) {
            filesI18n.addAll(UtilsWeb.findFiles(folderI18n, filterJson));
        }

        return filesI18n;
    }

    public ArrayList<File> getListI18nFilesProperties() {

        final ArrayList<File> filesI18n = new ArrayList<>();

        final FilenameFilter filterProperties = new FilenameFilter() {
            @Override
            public boolean accept(final File file, final String name) {
                return name.toLowerCase().endsWith(".properties");
            }
        };

        for(final File folderI18n : getListFoldersI18n()) {
            filesI18n.addAll(UtilsWeb.findFiles(folderI18n, filterProperties));
        }

        return filesI18n;
    }

}
