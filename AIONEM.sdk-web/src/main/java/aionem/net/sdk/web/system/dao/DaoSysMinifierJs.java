package aionem.net.sdk.web.system.dao;

import aionem.net.sdk.data.DaoRes;
import aionem.net.sdk.core.utils.UtilsText;
import aionem.net.sdk.data.utils.UtilsResource;
import aionem.net.sdk.web.beans.ConfEnv;
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

        final File fileIn = new File(inputFilePath);
        final String html = minify(UtilsText.toString(fileIn, true));

        final File fileOut = new File(outputFilePath);
        final boolean isSaved = UtilsWeb.writeFile(fileOut, html);

        resMinify.setSuccess(isSaved);

        return resMinify;
    }

    public static String minifyFolder(final File fileFolder, final boolean isSave) {

        final StringBuilder builderJs = new StringBuilder();

        final String uiFrontend = ConfEnv.getInstance().getContextPath("/ui.frontend");

        final File fileJs = new File(fileFolder, ".js");
        final File fileJsJsp = new File(fileFolder, "js.jsp");
        final Pattern pattern = Pattern.compile("(/ui\\.frontend[^\"']*\\.js)\"");

        final Matcher matcher = pattern.matcher(UtilsText.toString(fileJsJsp, true));

        final ArrayList<String> listFileJs = new ArrayList<>();
        while(matcher.find()) {
            listFileJs.add(matcher.group(1));
        }

        int n = 0;
        for(int i = 0; i < listFileJs.size(); i++) {

            final File file = new File(UtilsResource.getRealPathRoot(listFileJs.get(i)));

            if(file.exists() && file.isFile()) {

                String js = UtilsText.toString(file, true);

                if(!file.getName().equals("min.js")) {
                    js = minify(js);
                }

                if(isSave) {
                    UtilsWeb.writeFile(file, js);
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
            final boolean isMinified = UtilsWeb.writeFile(fileJs, builderJs.toString());
            // fileJsJsp.delete();
            // update templates: replace /js.jsp" = /.js"
            new File(fileFolder, "js").delete();

            log.error("\nAioWeb::Minify JS {} : {}", "ui.frontend/"+ fileFolder.getName(), isMinified);
        }

        if(isSave) {

            final FilenameFilter filterJs = new FilenameFilter() {
                @Override
                public boolean accept(final File file, final String name) {
                    return name.toLowerCase().endsWith(".js");
                }
            };

            final ArrayList<File> listFiles = UtilsWeb.findFiles(fileFolder, filterJs);

            for (File file : listFiles) {
                if (file.isFile()) {

                    String js = UtilsText.toString(file, true);

                    if (!file.getName().equals("min.js")) {
                        js = minify(js);
                    }

                    UtilsWeb.writeFile(file, js);
                }
            }
        }

        return builderJs.toString();
    }

    public static String minifyFile(File file) {
        return minify(UtilsText.toString(file, true));
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
