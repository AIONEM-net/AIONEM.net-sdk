package aionem.net.sdk.web.dao;

import aionem.net.sdk.data.utils.UtilsResource;
import aionem.net.sdk.web.utils.UtilsWeb;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;


public class ConfigManager {

    public ConfigManager() {

    }

    public ArrayList<File> getListFoldersConfig() {
        final ArrayList<File> folder = new ArrayList<>();

        final File folder1 = ResourceResolver.getRealFileWebInf("/ui.config/env").getFile();
        final File folder2 = UtilsResource.getResourceFile("/config");
        final File folder3 = UtilsResource.getResourceFolder();

        if(folder1.exists() && folder1.isDirectory()) folder.add(folder1);
        if(folder2.exists() && folder2.isDirectory()) folder.add(folder2);
        if(folder3.exists() && folder3.isDirectory()) folder.add(folder3);

        return folder;
    }

    public ArrayList<File> getListFilesConfig() {

        final ArrayList<File> listFilesConfig = new ArrayList<>();

        listFilesConfig.addAll(listFilesConfigJson());

        listFilesConfig.addAll(listFilesConfigProperties());

        return listFilesConfig;
    }

    public ArrayList<File> listFilesConfigJson() {

        final ArrayList<File> listFilesConfig = new ArrayList<>();

        final FilenameFilter filterJson = new FilenameFilter() {
            @Override
            public boolean accept(final File file, final String name) {
                return name.toLowerCase().endsWith(".json");
            }
        };

        for(final File folderConfig : getListFoldersConfig()) {
            listFilesConfig.addAll(UtilsWeb.findFiles(folderConfig, filterJson));
        }

        return listFilesConfig;
    }

    public ArrayList<File> listFilesConfigProperties() {

        final ArrayList<File> listFilesConfig = new ArrayList<>();

        final FilenameFilter filterProperties = new FilenameFilter() {
            @Override
            public boolean accept(final File file, final String name) {
                return name.toLowerCase().endsWith(".properties");
            }
        };

        for(final File folderConfig : getListFoldersConfig()) {
            listFilesConfig.addAll(UtilsWeb.findFiles(folderConfig, filterProperties));
        }

        return listFilesConfig;
    }

}
