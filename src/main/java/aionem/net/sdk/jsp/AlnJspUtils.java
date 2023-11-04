package aionem.net.sdk.jsp;

import aionem.net.sdk.core.utils.AlnUtilsText;
import lombok.extern.log4j.Log4j2;

import java.io.File;
import java.io.FileWriter;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;


@Log4j2
public class AlnJspUtils {

    public static String readEtcFile(final AlnJsp alnJsp, final String fileName) {
        return readFile(alnJsp, "/WEB-INF/ui.etc"+ fileName);
    }
    public static String readResourceFile(final AlnJsp alnJsp, final String fileName) {
        return readFile(alnJsp, "/WEB-INF/classes"+ fileName);
    }
    public static String readWebInfFile(final AlnJsp alnJsp, final String fileName) {
        return readFile(alnJsp, "/WEB-INF"+ fileName);
    }

    public static String readFile(final AlnJsp alnJsp, final String fileName) {
        try {
            final File file = new File(alnJsp.getRealPathRoot(fileName));
            if(!file.isDirectory()) {
                return AlnUtilsText.toString(file);
            }
        }catch(Exception e) {
            log.error("\nERROR: - readFile ::" + e +"\n");
        }
        return null;
    }

    public static String readResource(final AlnJsp alnJsp, final String fileName) {
        try {
            final InputStream inputStream = alnJsp.getResourceAsStream(fileName);
            if(inputStream != null) {
                return AlnUtilsText.toString(inputStream);
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

}
