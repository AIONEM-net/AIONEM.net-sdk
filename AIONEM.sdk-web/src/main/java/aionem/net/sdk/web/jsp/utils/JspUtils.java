package aionem.net.sdk.web.jsp.utils;

import aionem.net.sdk.core.utils.UtilsText;
import aionem.net.sdk.web.jsp.AioJsp;
import lombok.extern.log4j.Log4j2;

import java.io.File;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;


@Log4j2
public class JspUtils {

    public static String readEtcFile(final AioJsp aioJsp, final String fileName) {
        return readFile(aioJsp, "/WEB-INF/ui.app/etc"+ fileName);
    }
    public static String readResourceFile(final AioJsp aioJsp, final String fileName) {
        return readFile(aioJsp, "/WEB-INF/classes"+ fileName);
    }
    public static String readWebInfFile(final AioJsp aioJsp, final String fileName) {
        return readFile(aioJsp, "/WEB-INF"+ fileName);
    }

    public static String readFile(final AioJsp aioJsp, final String fileName) {
        try {
            final File file = new File(aioJsp.getRealPathRoot(fileName));
            if(!file.isDirectory()) {
                return UtilsText.toString(file);
            }
        }catch(Exception e) {
            log.error("\nERROR: - readFile ::" + e +"\n");
        }
        return null;
    }

    public static String readResource(final AioJsp aioJsp, final String fileName) {
        try {
            final InputStream inputStream = aioJsp.getResourceAsStream(fileName);
            if(inputStream != null) {
                return UtilsText.toString(inputStream);
            }
        }catch(Exception e) {
            log.error("\nERROR: - readResource ::" + e +"\n");
        }
        return null;
    }

    public static boolean writeFile(final String realPath, final String contents) {
        return writeFile(new File(realPath), contents);
    }
    public static boolean writeFile(final File file, final String contents) {
        boolean isWritten = false;
        try(final FileWriter fileWriter = new FileWriter(file, StandardCharsets.UTF_8)) {
            fileWriter.write(contents);
            isWritten = true;
        } catch (Exception e) {
            log.error("\nERROR: - writeFile ::"+ e +"\n");
        }
        return isWritten;
    }

    public static ArrayList<File> findFiles(final File fileFolder, final FilenameFilter filenameFilter) {

        final ArrayList<File> listFiles = new ArrayList<>();

        if(fileFolder.isDirectory()) {
            final File[] files = fileFolder.listFiles();
            if(files != null) {
                for(final File file : files) {
                    if(file.isDirectory()) {
                        listFiles.addAll(findFiles(file, filenameFilter));
                    } else {
                        if(filenameFilter.accept(fileFolder, file.getName())) {
                            listFiles.add(file);
                        }
                    }
                }
            }
        }

        return listFiles;
    }

}
