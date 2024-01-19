package aionem.net.sdk.web.dao;

import aionem.net.sdk.data.utils.UtilsResource;
import aionem.net.sdk.web.beans.Resource;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;


public class ConfManager {

    public ConfManager() {

    }

    public Resource getConf(final String name) {
        return new Resource("/ui.config/conf", name);
    }

    public ArrayList<Resource> getListFolders() {

        final ArrayList<Resource> listFolders = new ArrayList<>();

        final Resource folder1 = new Resource("/ui.config/conf");
        final Resource folder2 = new Resource(UtilsResource.getResourcePath("/ui.config/conf"));

        if(folder1.exists() && folder1.isFolder()) listFolders.add(folder1);
        if(folder2.exists() && folder2.isFolder()) listFolders.add(folder2);

        return listFolders;
    }

    public ArrayList<Resource> getListConfs() {

        final ArrayList<Resource> listConfs = new ArrayList<>();

        final FilenameFilter filterConf = new FilenameFilter() {
            @Override
            public boolean accept(final File file, final String name) {
                return name.toLowerCase().endsWith(".json") || name.toLowerCase().endsWith(".properties");
            }
        };

        for(final Resource folderConf : getListFolders()) {
            listConfs.addAll(ResourceResolver.findResources(folderConf, filterConf));
        }

        return listConfs;
    }

}
