package aionem.net.sdk.web.system.dao;

import aionem.net.sdk.data.beans.DaoRes;
import aionem.net.sdk.core.utils.UtilsText;
import aionem.net.sdk.web.beans.Resource;
import aionem.net.sdk.web.utils.UtilsWeb;
import com.googlecode.htmlcompressor.compressor.HtmlCompressor;
import lombok.extern.log4j.Log4j2;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;


@Log4j2
public class DaoSysMinifierHtml {

    public static DaoRes minifySave(final String pathIn) {
        return minifySave(pathIn, pathIn);
    }

    public static DaoRes minifySave(final String inputFilePath, final String outputFilePath) {

        final DaoRes resMinify = new DaoRes();

        final Resource fileIn = new Resource(inputFilePath);
        final String html = minify(fileIn.readContent(false));

        final Resource fileOut = new Resource(outputFilePath);
        final boolean isSaved = UtilsWeb.writeResource(fileOut, html);

        resMinify.setSuccess(isSaved);

        return resMinify;
    }

    public static String minifyFolder(final Resource fileFolder, final boolean isSaved) {

        final StringBuilder htmlBuilder = new StringBuilder();

        final FilenameFilter htmlFilter = new FilenameFilter() {
            @Override
            public boolean accept(final File file, final String name) {
                return name.toLowerCase().endsWith(".html");
            }
        };

        final ArrayList<Resource> listFiles = UtilsWeb.findResources(fileFolder, htmlFilter);

        for(int i = 0; i < listFiles.size(); i++) {

            final Resource file = listFiles.get(i);

            if(file.isFile()) {

                final String html = minifyFile(file);

                htmlBuilder.append(i > 0 ? "\n" : "").append(html);

                if(isSaved) {
                    UtilsWeb.writeResource(file, html);
                }
            }
        }

        return htmlBuilder.toString();
    }

    public static String minifyFile(final Resource file) {
        return minify(file.readContent(false));
    }

    public static String minify(String html) {
        try {

            HtmlCompressor compressor = new HtmlCompressor();

            html = compressor.compress(html);

            html = html
                    .replace("/ui.page", "")

                    .replace("background-image: unset;", "")
                    .replace("--fontSize1: unset;", "")
                    .replace("--fontSize2: unset;", "")
                    .replace("--fontWeight1: unset;", "")
                    .replace("--fontWeight2: unset;", "")
                    .replace("--color1: none;", "")
                    .replace("--color2: none;", "")
                    .replace("--colorHover1: none;", "")
                    .replace("--colorHover2: none;", "")
                    .replace("---webkit-text-fill-color: unset;", "")
                    .replace("--colorBackground1: unset;", "")
                    .replace("--colorBackground2: unset;", "")
                    .replace("--colorBackgroundHover1: unset;", "")
                    .replace("--colorBackgroundHover2: ;", "")
                    .replace("--borderRadius: -px;", "")
                    .replace("--borderRadiusTopLeft:: -px;", "")
                    .replace("--borderRadiusTopRight: -px;", "")
                    .replace("--borderRadiusBottomLeft: -px;", "")
                    .replace("--borderRadiusBottomRight: -px;", "")
                    .replace("--borderWidth: -px;", "")
                    .replace("--borderWidthLeft: -px;", "")
                    .replace("--borderWidthRight: -px;", "")
                    .replace("--borderWidthTop: -px;", "")
                    .replace("--borderWidthBottom: -px;", "")
                    .replace("--borderColor1: unset;", "")
                    .replace("--borderColor2: none;", "")
                    .replace("--borderColorMenu1: none;", "")
                    .replace("--borderColorMenu2: none;", "")
            ;

        }catch(final Exception e) {
            log.error("AIONEM.Jsp_ERROR: JspMinifyHtml - minify :: {}", e.getMessage());
        }
        return html;
    }

}
