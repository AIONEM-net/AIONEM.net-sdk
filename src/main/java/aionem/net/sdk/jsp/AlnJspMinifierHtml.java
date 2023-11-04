package aionem.net.sdk.jsp;

import aionem.net.sdk.core.api.AlnDaoRes;
import aionem.net.sdk.core.utils.AlnUtilsText;
import com.googlecode.htmlcompressor.compressor.HtmlCompressor;
import lombok.extern.log4j.Log4j2;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;


@Log4j2
public class AlnJspMinifierHtml {

    public static AlnDaoRes minifySave(final String pathIn) throws Exception {
        return minifySave(pathIn, pathIn);
    }
    public static AlnDaoRes minifySave(final String inputFilePath, final String outputFilePath) throws Exception {

        final AlnDaoRes resMinify = new AlnDaoRes();

        final File fileIn = new File(inputFilePath);
        final String html = minify(AlnUtilsText.toString(fileIn));

        final File fileOut = new File(outputFilePath);
        final boolean isSaved = AlnJspUtils.writeFile(fileOut, html);

        resMinify.setSuccess(isSaved);

        return resMinify;
    }

    public static String minifyFolder(final File fileFolder, final boolean isSaved) {

        final StringBuilder htmlBuilder = new StringBuilder();

        final FilenameFilter htmlFilter = new FilenameFilter() {
            @Override
            public boolean accept(final File file, final String name) {
                return name.toLowerCase().endsWith(".html");
            }
        };

        final ArrayList<File> listFiles = AlnJspUtils.findFiles(fileFolder, htmlFilter);

        for(int i = 0; i < listFiles.size(); i++) {

            final File file = listFiles.get(i);

            if(file.isFile()) {

                final String html = minifyFile(file);

                htmlBuilder.append(i > 0 ? "\n" : "").append(html);

                if(isSaved) {
                    AlnJspUtils.writeFile(file, html);
                }
            }
        }

        return htmlBuilder.toString();
    }

    public static String minifyFile(File file) {
        return minify(AlnUtilsText.toString(file));
    }

    public static String minify(String html) {
        try {

            HtmlCompressor compressor = new HtmlCompressor();

            html = compressor.compress(html);

        }catch(final Exception e) {
            log.error("AIONEM.Jsp_ERROR: AlnJspMinifyHtml - minify :: {}", e.getMessage());
        }
        return html;
    }

}
