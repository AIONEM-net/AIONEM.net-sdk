package aionem.net.sdk.web.system.dao;

import aionem.net.sdk.core.utils.UtilsText;
import aionem.net.sdk.data.beans.DaoRes;
import aionem.net.sdk.web.beans.Resource;
import aionem.net.sdk.web.config.ConfEnv;
import aionem.net.sdk.web.dao.ResourceResolver;
import com.google.javascript.jscomp.Compiler;
import com.google.javascript.jscomp.*;
import lombok.extern.log4j.Log4j2;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;


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
        final boolean isSaved = fileOut.saveContent(html);

        resMinify.setSuccess(isSaved);

        return resMinify;
    }

    public static String minifyFolder(final Resource resourceFrontend, final boolean isSave) {

        final StringBuilder builderJs = new StringBuilder();

        final String uiFrontend = ConfEnv.getInstance().getContextPath("/ui.frontend");

        final Resource fileJs = new Resource(resourceFrontend, ".js");
        final ArrayList<String> listFileJs = resourceFrontend.getProperties().getArray("js");

        int n = 0;
        for(int i = 0; i < listFileJs.size(); i++) {

            final Resource file = resourceFrontend.child("js", listFileJs.get(i));

            if(file.exists() && file.isFile()) {

                String js = file.readContent(true);

                if(!file.getName().equals("min.js")) {
                    js = minify(js);
                }

                if(isSave) {
                    file.saveContent(js);
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
            final boolean isMinified = fileJs.saveContent(builderJs.toString());
            new Resource(resourceFrontend, "js").delete();

            log.error("\nAioWeb::Minify JS {} : {}", "ui.frontend/"+ resourceFrontend.getName(), isMinified);
        }

        if(isSave) {

            final FilenameFilter filterJs = new FilenameFilter() {
                @Override
                public boolean accept(final File file, final String name) {
                    return name.toLowerCase().endsWith(".js");
                }
            };

            final ArrayList<Resource> listFiles = ResourceResolver.findResources(resourceFrontend, filterJs);

            for(Resource file : listFiles) {
                if(file.isFile()) {

                    String js = file.readContent(true);

                    if(!file.getName().equals("min.js")) {
                        js = minify(js);
                    }

                    file.saveContent(js);
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
