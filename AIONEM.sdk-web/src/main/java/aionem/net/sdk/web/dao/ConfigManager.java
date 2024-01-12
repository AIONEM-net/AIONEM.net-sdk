package aionem.net.sdk.web.dao;

import aionem.net.sdk.data.utils.UtilsResource;
import aionem.net.sdk.web.beans.Resource;
import aionem.net.sdk.web.utils.UtilsWeb;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;


public class ConfigManager {

    public ConfigManager() {

    }

    public ArrayList<Resource> getListFolders() {
        final ArrayList<Resource> folder = new ArrayList<>();

        final File folder1 = ResourceResolver.getRealFileWebInf("/ui.config/env").getFile();
        final File folder2 = UtilsResource.getResourceFile("/config");
        final File folder3 = UtilsResource.getResourceFolder();

        if(folder1.exists() && folder1.isDirectory()) folder.add(new Resource(folder1));
        if(folder2.exists() && folder2.isDirectory()) folder.add(new Resource(folder2));
        if(folder3.exists() && folder3.isDirectory()) folder.add(new Resource(folder3));

        return folder;
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
            listConfigs.addAll(UtilsWeb.findResources(folderConfig, filterJson));
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
            listConfigs.addAll(UtilsWeb.findResources(folderConfig, filterProperties));
        }

        return listConfigs;
    }

}
