package aionem.net.sdk.web.dao;


import aionem.net.sdk.core.utils.UtilsText;
import aionem.net.sdk.web.utils.UtilsWeb;

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
}
