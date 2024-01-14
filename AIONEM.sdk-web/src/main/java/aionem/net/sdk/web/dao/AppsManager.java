package aionem.net.sdk.web.dao;

import aionem.net.sdk.web.beans.Resource;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;


public class AppsManager {

    public AppsManager() {

    }

    public Resource getFolder() {
        return new Resource("/WEB-INF/ui.apps");
    }

    public ArrayList<Resource> listApps() {

        final ArrayList<Resource> listApps = new ArrayList<>();

        for(final Resource resourceApp : getFolder().children()) {
            if(resourceApp.isFolder()) {
                listApps.add(resourceApp);
            }
        }

        return listApps;
    }

    public ArrayList<Resource> listComponents() {

        final ArrayList<Resource> listComponents = new ArrayList<>();

        for(final Resource resourceApp : listApps()) {
            listComponents.addAll(listComponents(resourceApp));
        }

        return listComponents;
    }

    public ArrayList<Resource> listComponents(final Resource resourceApp) {

        final ArrayList<Resource> listComponents = new ArrayList<>();

        if(resourceApp.isApps()) {

            final FilenameFilter filterJsp = new FilenameFilter() {
                @Override
                public boolean accept(final File file, final String name) {
                    return name.equals(".jsp");
                }
            };

            for (final Resource resourceCmp : ResourceResolver.findResources(resourceApp, filterJsp)) {
                listComponents.add(resourceCmp.getParent());
            }

        }

        return listComponents;
    }

}
