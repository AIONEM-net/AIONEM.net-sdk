package aionem.net.sdk.web.system.deploy;

import aionem.net.sdk.data.DaoRes;
import aionem.net.sdk.core.utils.UtilsText;
import aionem.net.sdk.web.AioWeb;
import aionem.net.sdk.web.utils.UtilsWeb;
import com.google.javascript.jscomp.Compiler;
import com.google.javascript.jscomp.*;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class MinifierJs {


    public static DaoRes minifySave(final String pathInOut) throws Exception {
        return minifySave(pathInOut, pathInOut);
    }
    public static DaoRes minifySave(final String inputFilePath, final String outputFilePath) throws Exception {

        final DaoRes resMinify = new DaoRes();

        final File fileIn = new File(inputFilePath);
        final String html = minify(UtilsText.toString(fileIn));

        final File fileOut = new File(outputFilePath);
        final boolean isSaved = UtilsWeb.writeFile(fileOut, html);

        resMinify.setSuccess(isSaved);

        return resMinify;
    }

    public static String minifyFolder(final AioWeb aioWeb, final File fileFolder, final boolean isSave) {

        final StringBuilder builderJs = new StringBuilder();

        final String uiFrontend = aioWeb.getContextPath("/ui.frontend");

        final File fileJs = new File(fileFolder, ".js");
        final File fileJsJsp = new File(fileFolder, "js.jsp");
        final Pattern pattern = Pattern.compile("(/ui\\.frontend[^\"']*\\.js)\"");

        final Matcher matcher = pattern.matcher(UtilsText.toString(fileJsJsp));

        final ArrayList<String> listFileJs = new ArrayList<>();
        while(matcher.find()) {
            listFileJs.add(matcher.group(1));
        }
        for(int i = 0; i < listFileJs.size(); i++) {

            final File file = new File(aioWeb.getRealPathRoot(listFileJs.get(i)));

            String js = UtilsText.toString(file, true);

            if(!file.getName().equals("min.js")) {
                // js = minifyFile(file);
            }

            if(isSave) {
                UtilsWeb.writeFile(file, js);
            }

            js = js
                    .replace("(/ui.frontend", "("+ uiFrontend)
                    .replace("\"/ui.frontend", "\""+ uiFrontend)
                    .replace("'/ui.frontend", "'"+ uiFrontend)
                    .replace("`/ui.frontend", "`"+ uiFrontend)
                    .replace("../", "");

            if(!UtilsText.isEmpty(js)) {
                builderJs.append(i > 0 ? "\n" : "").append(js);
            }
        }
        if(isSave) {
            UtilsWeb.writeFile(fileJs, builderJs.toString());
        }

        if(isSave) {

            final FilenameFilter filterJs = new FilenameFilter() {
                @Override
                public boolean accept(final File file, final String name) {
                    return name.toLowerCase().endsWith(".js");
                }
            };

            final ArrayList<File> listFiles = UtilsWeb.findFiles(fileFolder, filterJs);

            for(int i = 0; i < listFiles.size(); i++) {
                File file = listFiles.get(i);
                if(file.isFile()) {

                    String js = UtilsText.toString(file, true);

                    if(!file.getName().equals("min.js")) {
                        // js = minifyFile(file);
                    }

                    UtilsWeb.writeFile(file, js);
                }
            }
        }

        return builderJs.toString();
    }

    public static String minifyFile(File file) {
        return minify(UtilsText.toString(file));
    }

    public static String minify(String js) {

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
