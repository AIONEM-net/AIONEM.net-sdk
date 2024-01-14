package aionem.net.sdk.web.system.dao;

import aionem.net.sdk.data.beans.DaoRes;
import aionem.net.sdk.web.beans.Resource;
import aionem.net.sdk.web.dao.ResourceResolver;
import com.googlecode.htmlcompressor.compressor.HtmlCompressor;
import lombok.extern.log4j.Log4j2;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;


@Log4j2
public class DaoSysMinifierImage {

    public static DaoRes minifySave(final String pathIn) throws Exception {
        return minifySave(pathIn, pathIn);
    }
    public static DaoRes minifySave(final String inputFilePath, final String outputFilePath) throws Exception {

        final DaoRes resMinify = new DaoRes();

        final Resource fileIn = new Resource(inputFilePath);
        final String html = minify(fileIn.readContent());

        final Resource fileOut = new Resource(outputFilePath);
        final boolean isSaved = fileOut.saveContent(html);

        resMinify.setSuccess(isSaved);

        return resMinify;
    }

    public static String minifyFolder(final Resource resourceFolder, final boolean isSaved) {

        final StringBuilder htmlBuilder = new StringBuilder();

        final FilenameFilter htmlFilter = new FilenameFilter() {
            @Override
            public boolean accept(final File file, final String name) {
                final String fileName = file.getName().toLowerCase();
                return fileName.endsWith(".jpg") || fileName.endsWith(".jpeg") ||
                        fileName.endsWith(".png") || fileName.endsWith(".gif") ||
                        fileName.endsWith(".bmp") || fileName.endsWith(".webp");
            }
        };

        final ArrayList<Resource> listFiles = ResourceResolver.findResources(resourceFolder, htmlFilter);

        for(int i = 0; i < listFiles.size(); i++) {

            final Resource file = listFiles.get(i);

            if(file.isFile()) {

                final String html = minifyFile(file);

                htmlBuilder.append(i > 0 ? "\n" : "").append(html);

                if(isSaved) {
                    file.saveContent(html);
                }
            }
        }

        return htmlBuilder.toString();
    }

    public static String minifyFile(Resource file) {
        return minify(file.readContent());
    }

    public static String minify(String html) {
        try {

            HtmlCompressor compressor = new HtmlCompressor();

            html = compressor.compress(html);

        }catch(final Exception e) {
            log.error("AIONEM.Jsp_ERROR: JspMinifierImage - minify :: {}", e.getMessage());
        }
        return html;
    }

}
