package aionem.net.sdk.web.dao;

import aionem.net.sdk.data.utils.UtilsResource;
import aionem.net.sdk.web.beans.Resource;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;


public class I18nManager {

    public I18nManager() {

    }

    public Resource getI18n(final String name) {
        return new Resource(ResourceResolver.getRealPathWebInf(UtilsResource.path("/ui.config/i18n", name)));
    }

    public ArrayList<Resource> getListFolders() {

        final ArrayList<Resource> listFolders = new ArrayList<>();

        final Resource folder1 = ResourceResolver.getRealFileWebInf("/ui.config/i18n");
        final Resource folder2 = new Resource(UtilsResource.getResourcePath("/ui.config/i18n"));

        if(folder1.exists() && folder1.isFolder()) listFolders.add(folder1);
        if(folder2.exists() && folder2.isFolder()) listFolders.add(folder2);

        return listFolders;
    }

    public ArrayList<Resource> getListI18n() {

        final ArrayList<Resource> listI18n = new ArrayList<>();

        listI18n.addAll(getListI18nJson());

        listI18n.addAll(getListI18nProperties());

        return listI18n;
    }

    public ArrayList<Resource> getListI18nJson() {

        final ArrayList<Resource> listI18n = new ArrayList<>();

        final FilenameFilter filterJson = new FilenameFilter() {
            @Override
            public boolean accept(final File file, final String name) {
                return name.toLowerCase().endsWith(".json");
            }
        };

        for(final Resource folderI18n : getListFolders()) {
            listI18n.addAll(ResourceResolver.findResources(folderI18n, filterJson));
        }

        return listI18n;
    }

    public ArrayList<Resource> getListI18nProperties() {

        final ArrayList<Resource> listI18n = new ArrayList<>();

        final FilenameFilter filterProperties = new FilenameFilter() {
            @Override
            public boolean accept(final File file, final String name) {
                return name.toLowerCase().endsWith(".properties");
            }
        };

        for(final Resource folderI18n : getListFolders()) {
            listI18n.addAll(ResourceResolver.findResources(folderI18n, filterProperties));
        }

        return listI18n;
    }

}
