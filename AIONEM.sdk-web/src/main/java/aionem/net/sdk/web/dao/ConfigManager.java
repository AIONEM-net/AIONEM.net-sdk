package aionem.net.sdk.web.dao;

import aionem.net.sdk.data.utils.UtilsResource;
import aionem.net.sdk.web.beans.Resource;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;


public class ConfigManager {

    public ConfigManager() {

    }

    public Resource getConfig(final String name) {
        return new Resource(ResourceResolver.getRealPathWebInf(UtilsResource.path("/ui.config/env", name)));
    }

    public ArrayList<Resource> getListFolders() {

        final ArrayList<Resource> listFolders = new ArrayList<>();

        final Resource folder1 = ResourceResolver.getRealFileWebInf("/ui.config/env");
        final Resource folder2 = new Resource(UtilsResource.getResourcePath("/ui.config/env"));

        if(folder1.exists() && folder1.isFolder()) listFolders.add(folder1);
        if(folder2.exists() && folder2.isFolder()) listFolders.add(folder2);

        return listFolders;
    }

    public ArrayList<Resource> getListConfigs() {

        final ArrayList<Resource> listConfigs = new ArrayList<>();

        listConfigs.addAll(listConfigJson());

        listConfigs.addAll(listConfigProperties());

        return listConfigs;
    }

    public ArrayList<Resource> listConfigJson() {

        final ArrayList<Resource> listConfigs = new ArrayList<>();

        final FilenameFilter filterJson = new FilenameFilter() {
            @Override
            public boolean accept(final File file, final String name) {
                return name.toLowerCase().endsWith(".json");
            }
        };

        for(final Resource folderConfig : getListFolders()) {
            listConfigs.addAll(ResourceResolver.findResources(folderConfig, filterJson));
        }

        return listConfigs;
    }

    public ArrayList<Resource> listConfigProperties() {

        final ArrayList<Resource> listConfigs = new ArrayList<>();

        final FilenameFilter filterProperties = new FilenameFilter() {
            @Override
            public boolean accept(final File file, final String name) {
                return name.toLowerCase().endsWith(".properties");
            }
        };

        for(final Resource folderConfig : getListFolders()) {
            listConfigs.addAll(ResourceResolver.findResources(folderConfig, filterProperties));
        }

        return listConfigs;
    }

}
