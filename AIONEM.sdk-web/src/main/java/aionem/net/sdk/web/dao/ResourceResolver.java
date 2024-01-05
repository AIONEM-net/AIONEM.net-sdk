package aionem.net.sdk.web.dao;


import aionem.net.sdk.core.utils.UtilsText;
import aionem.net.sdk.data.utils.UtilsResource;
import aionem.net.sdk.web.modals.ConfEnv;
import aionem.net.sdk.web.utils.UtilsWeb;

import java.io.File;
import java.nio.file.Path;

public class ResourceResolver {

    public static int references(final Path pathFile, final String pathOld, final String pathNew, final boolean update) {
        final int[] references = new int[]{-1};

        final Runnable runnable = new Runnable() {
            @Override
            public void run() {

                final String content = UtilsText.toString(pathFile);

                final String updatedContent = content.replace("ui.page"+ pathOld,"ui.page"+ pathNew);

                references[0] = (content.length() - updatedContent.length()) / (pathOld.length() - pathNew.length());

                if(update) {
                    UtilsWeb.writeFile(pathFile.toFile(), updatedContent);
                }

            }
        };

        if(update) {
            new Thread(runnable).start();
        }else {
            runnable.run();
        }

        return Math.abs(references[0]);
    }

    public static String getRealPathWebInf() {
        return getRealPathWebInf("");
    }

    public static String getRealPathWebInf(final String path) {
        return UtilsResource.getRealPathParent(path);
    }

    public static File getRealFileWebInf(final String path) {
        return new File(getRealPathWebInf(path));
    }

    public static String getRealPathPage() {
        return UtilsResource.getRealPathRoot("/ui.page");
    }

    public static String getRealPathDrive() {
        return UtilsResource.getRealPathRoot("/ui.drive");
    }

    public static String getRealPathDrive(final String path) {
        return UtilsResource.getRealPathRoot("/ui.drive"+ (!UtilsText.isEmpty(path) ? "/" + path : ""));
    }

    public static String getRealPathPage(final String path) {
        return UtilsResource.getRealPathRoot("/ui.page"+ (!UtilsText.isEmpty(path) || path.equals("/") ? path : ConfEnv.getHome()));
    }

}
