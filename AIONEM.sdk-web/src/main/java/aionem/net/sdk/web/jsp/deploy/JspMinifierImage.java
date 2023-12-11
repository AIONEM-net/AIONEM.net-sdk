package aionem.net.sdk.web.jsp.deploy;

import aionem.net.sdk.data.api.DaoRes;
import aionem.net.sdk.core.utils.UtilsText;
import aionem.net.sdk.web.jsp.utils.JspUtils;
import com.googlecode.htmlcompressor.compressor.HtmlCompressor;
import lombok.extern.log4j.Log4j2;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;


@Log4j2
public class JspMinifierImage {

    public static DaoRes minifySave(final String pathIn) throws Exception {
        return minifySave(pathIn, pathIn);
    }
    public static DaoRes minifySave(final String inputFilePath, final String outputFilePath) throws Exception {

        final DaoRes resMinify = new DaoRes();

        final File fileIn = new File(inputFilePath);
        final String html = minify(UtilsText.toString(fileIn));

        final File fileOut = new File(outputFilePath);
        final boolean isSaved = JspUtils.writeFile(fileOut, html);

        resMinify.setSuccess(isSaved);

        return resMinify;
    }

    public static String minifyFolder(final File fileFolder, final boolean isSaved) {

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

        final ArrayList<File> listFiles = JspUtils.findFiles(fileFolder, htmlFilter);

        for(int i = 0; i < listFiles.size(); i++) {

            final File file = listFiles.get(i);

            if(file.isFile()) {

                final String html = minifyFile(file);

                htmlBuilder.append(i > 0 ? "\n" : "").append(html);

                if(isSaved) {
                    JspUtils.writeFile(file, html);
                }
            }
        }

        return htmlBuilder.toString();
    }

    public static String minifyFile(File file) {
        return minify(UtilsText.toString(file));
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
