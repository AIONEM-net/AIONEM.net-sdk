package aionem.net.sdk.web.utils;

import aionem.net.sdk.core.utils.UtilsText;
import aionem.net.sdk.web.AioWeb;
import lombok.extern.log4j.Log4j2;

import java.io.File;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;


@Log4j2
public class UtilsWeb {


    public static String readFileWebInfEtc(final AioWeb aioWeb, final String fileName) {
        return readFile(aioWeb, "/WEB-INF/ui.app", "/etc/"+ fileName);
    }
    public static String readFileResource(final AioWeb aioWeb, final String fileName) {
        return readFile(aioWeb, "/WEB-INF/classes/", fileName);
    }
    public static String readFileWebInf(final AioWeb aioWeb, final String fileName) {
        return readFile(aioWeb, "/WEB-INF/", fileName);
    }
    public static String readFileWebInfConfig(final AioWeb aioWeb, final String fileName) {
        return readFile(aioWeb, "/WEB-INF/ui.config/", fileName);
    }
    public static String readFileWebInfEnv(final AioWeb aioWeb, final String fileName) {
        return readFile(aioWeb, "/WEB-INF/ui.config/env/", fileName);
    }
    public static String readFileWebInfI18n(final AioWeb aioWeb, final String fileName) {
        return readFile(aioWeb, "/WEB-INF/ui.config", "/i18n/"+ fileName);
    }

    public static String readFile(final AioWeb aioWeb, final String folderName, final String fileName) {
        try {
            if(aioWeb != null) {
                final File file = new File(aioWeb.getRealPathRoot(folderName + fileName));
                if(!file.isDirectory() && file.exists()) {
                    return UtilsText.toString(file);
                }
            }else {
                final InputStream inputStream = new AioWeb().getResourceAsStream(folderName + fileName);
                if(inputStream != null) {
                    return UtilsText.toString(inputStream);
                }
            }
        }catch(Exception e) {
            log.error("\nERROR: - readFile ::" + e + folderName + fileName +"\n");
        }
        return readResource(aioWeb, fileName);
    }

    public static String readResource(final AioWeb aioWeb, final String fileName) {
        try {
            final InputStream inputStream = (aioWeb == null ? new AioWeb() : aioWeb).getResourceAsStream(fileName);
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
