package aionem.net.sdk.jsp;

import aionem.net.sdk.core.api.AlnDaoRes;
import aionem.net.sdk.core.utils.AlnUtilsText;
import com.google.javascript.jscomp.Compiler;
import com.google.javascript.jscomp.*;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class AlnJspMinifierJs {


    public static AlnDaoRes minifySave(final String pathInOut) throws Exception {
        return minifySave(pathInOut, pathInOut);
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

    public static String minifyFolder(final AlnJsp alnJsp, final File fileFolder, final boolean isSave) {

        final StringBuilder builderJs = new StringBuilder();

        final String uiFrontend = alnJsp.getContextPath("/ui.frontend");

        final File fileJs = new File(fileFolder, ".js");
        final File fileJsJsp = new File(fileFolder, "js.jsp");
        final Pattern pattern = Pattern.compile("(/ui\\.frontend[^\"']*\\.js)\"");

        final Matcher matcher = pattern.matcher(AlnUtilsText.toString(fileJsJsp));

        final ArrayList<String> listFileJs = new ArrayList<>();
        while(matcher.find()) {
            listFileJs.add(matcher.group(1));
        }
        for(int i = 0; i < listFileJs.size(); i++) {

            final File file = new File(alnJsp.getRealPathRoot(listFileJs.get(i)));

            String js = AlnUtilsText.toString(file, true);

            if(!file.getName().equals("min.js")) {
                // js = minifyFile(file);
            }

            if(isSave) {
                AlnJspUtils.writeFile(file, js);
            }

            js = js
                    .replace("(/ui.frontend", "("+ uiFrontend)
                    .replace("\"/ui.frontend", "\""+ uiFrontend)
                    .replace("'/ui.frontend", "'"+ uiFrontend)
                    .replace("`/ui.frontend", "`"+ uiFrontend)
                    .replace("../", "");

            if(!AlnUtilsText.isEmpty(js)) {
                builderJs.append(i > 0 ? "\n" : "").append(js);
            }
        }
        if(isSave) {
            AlnJspUtils.writeFile(fileJs, builderJs.toString());
        }

        final FilenameFilter filterJs = new FilenameFilter() {
            @Override
            public boolean accept(final File file, final String name) {
                return name.endsWith(".js");
            }
        };
        if(isSave) {
            final File[] listFiles = fileFolder.listFiles(filterJs);
            for (int i = 0; i < listFiles.length; i++) {
                File file = listFiles[i];
                if (file.isFile()) {

                    String js = AlnUtilsText.toString(file, true);

                    if(!file.getName().equals("min.js")) {
                        // js = minifyFile(file);
                    }

                    AlnJspUtils.writeFile(file, js);
                }
            }
        }

        return builderJs.toString();
    }

    public static String minifyFile(File file) {
        return minify(AlnUtilsText.toString(file));
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
