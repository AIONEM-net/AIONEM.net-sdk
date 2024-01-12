package aionem.net.sdk.web.system.dao;

import aionem.net.sdk.data.beans.DaoRes;
import aionem.net.sdk.core.utils.UtilsText;
import aionem.net.sdk.data.utils.UtilsResource;
import aionem.net.sdk.web.beans.Resource;
import aionem.net.sdk.web.config.ConfEnv;
import aionem.net.sdk.web.utils.UtilsWeb;
import com.google.javascript.jscomp.Compiler;
import com.google.javascript.jscomp.*;
import lombok.extern.log4j.Log4j2;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@Log4j2
public class DaoSysMinifierJs {


    public static DaoRes minifySave(final String pathInOut) {
        return minifySave(pathInOut, pathInOut);
    }

    public static DaoRes minifySave(final String inputFilePath, final String outputFilePath) {

        final DaoRes resMinify = new DaoRes();

        final Resource fileIn = new Resource(inputFilePath);
        final String html = minify(fileIn.readContent(true));

        final Resource fileOut = new Resource(outputFilePath);
        final boolean isSaved = UtilsWeb.writeResource(fileOut, html);

        resMinify.setSuccess(isSaved);

        return resMinify;
    }

    public static String minifyFolder(final Resource fileFolder, final boolean isSave) {

        final StringBuilder builderJs = new StringBuilder();

        final String uiFrontend = ConfEnv.getInstance().getContextPath("/ui.frontend");

        final Resource fileJs = new Resource(fileFolder, ".js");
        final Resource fileJsJsp = new Resource(fileFolder, "js.jsp");
        final Pattern pattern = Pattern.compile("(/ui\\.frontend[^\"']*\\.js)\"");

        final Matcher matcher = pattern.matcher(fileJsJsp.readContent(true));

        final ArrayList<String> listFileJs = new ArrayList<>();
        while(matcher.find()) {
            listFileJs.add(matcher.group(1));
        }

        int n = 0;
        for(int i = 0; i < listFileJs.size(); i++) {

            final Resource file = new Resource(UtilsResource.getRealPathRoot(listFileJs.get(i)));

            if(file.exists() && file.isFile()) {

                String js = file.readContent(true);

                if(!file.getName().equals("min.js")) {
                    js = minify(js);
                }

                if(isSave) {
                    UtilsWeb.writeResource(file, js);
                    file.delete();
                }

                js = js
                        .replace("(/ui.frontend", "(" + uiFrontend)
                        .replace("\"/ui.frontend", "\"" + uiFrontend)
                        .replace("'/ui.frontend", "'" + uiFrontend)
                        .replace("`/ui.frontend", "`" + uiFrontend)
                        .replace("../", "");

                if(!UtilsText.isEmpty(js)) {
                    builderJs.append(i > 0 ? "\n" : "").append(js);
                }

                n++;
            }

        }
        if(isSave && n > 0) {
            final boolean isMinified = UtilsWeb.writeResource(fileJs, builderJs.toString());
            // fileJsJsp.delete();
            // update templates: replace /js.jsp" = /.js"
            new Resource(fileFolder, "js").delete();

            log.error("\nAioWeb::Minify JS {} : {}", "ui.frontend/"+ fileFolder.getName(), isMinified);
        }

        if(isSave) {

            final FilenameFilter filterJs = new FilenameFilter() {
                @Override
                public boolean accept(final File file, final String name) {
                    return name.toLowerCase().endsWith(".js");
                }
            };

            final ArrayList<Resource> listFiles = UtilsWeb.findResources(fileFolder, filterJs);

            for (Resource file : listFiles) {
                if (file.isFile()) {

                    String js = file.readContent(true);

                    if (!file.getName().equals("min.js")) {
                        js = minify(js);
                    }

                    UtilsWeb.writeResource(file, js);
                }
            }
        }

        return builderJs.toString();
    }

    public static String minifyFile(Resource file) {
        return minify(file.readContent(true));
    }

    public static String minify(String js) {
        if(true) return js;

        final Compiler compiler = new Compiler();
        compiler.disableThreads();

        final CompilerOptions copts = new CompilerOptions();
        copts.setWarningLevel(DiagnosticGroups.CONST, CheckLevel.OFF);

        final List<SourceFile> listExternals = new ArrayList<>();
        listExternals.add(SourceFile.fromCode("external.js", ""));

        final List<SourceFile> listInputs = new ArrayList<>();
        listInputs.add(SourceFile.fromCode("input.js", js));

        final Result result = compiler.compile(listExternals, listInputs, copts);

        if(result.success) {
            js = compiler.toSource();
        }

        return js;
    }

}
