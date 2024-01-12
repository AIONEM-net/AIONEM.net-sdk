package aionem.net.sdk.web.dao;

import aionem.net.sdk.data.utils.UtilsResource;
import aionem.net.sdk.web.beans.Resource;
import aionem.net.sdk.web.utils.UtilsWeb;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;


public class I18nManager {

    public I18nManager() {

    }

    public ArrayList<Resource> getListFolders() {

        final ArrayList<Resource> listI18nFolders = new ArrayList<>();

        final File folder1 = ResourceResolver.getRealFileWebInf("/ui.config/i18n").getFile();
        final File folder2 = UtilsResource.getResourceFile("/config/i18n");
        final File folder3 = UtilsResource.getResourceFile("/i18n");

        if(folder1.exists() && folder1.isDirectory()) listI18nFolders.add(new Resource(folder1));
        if(folder2.exists() && folder2.isDirectory()) listI18nFolders.add(new Resource(folder2));
        if(folder3.exists() && folder3.isDirectory()) listI18nFolders.add(new Resource(folder3));

        return listI18nFolders;
    }

    public ArrayList<Resource> getListI18n() {

        final ArrayList<Resource> filesI18n = new ArrayList<>();

        filesI18n.addAll(getListI18nJson());

        filesI18n.addAll(getListI18nProperties());

        return filesI18n;
    }

    public ArrayList<Resource> getListI18nJson() {

        final ArrayList<Resource> filesI18n = new ArrayList<>();

        final FilenameFilter filterJson = new FilenameFilter() {
            @Override
            public boolean accept(final File file, final String name) {
                return name.toLowerCase().endsWith(".json");
            }
        };

        for(final Resource folderI18n : getListFolders()) {
            filesI18n.addAll(UtilsWeb.findResources(folderI18n, filterJson));
        }

        return filesI18n;
    }

    public ArrayList<Resource> getListI18nProperties() {

        final ArrayList<Resource> filesI18n = new ArrayList<>();

        final FilenameFilter filterProperties = new FilenameFilter() {
            @Override
            public boolean accept(final File file, final String name) {
                return name.toLowerCase().endsWith(".properties");
            }
        };

        for(final Resource folderI18n : getListFolders()) {
            filesI18n.addAll(UtilsWeb.findResources(folderI18n, filterProperties));
        }

        return filesI18n;
    }

}
