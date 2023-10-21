package aionem.net.sdk.core.utils;

import aionem.net.sdk.jsp.AlnJsp;
import lombok.extern.log4j.Log4j2;

import java.io.File;
import java.io.InputStream;


@Log4j2
public class AlnUtilsJsp {

    public static String readResourceFile(final AlnJsp alnJsp, final String fileName) {
        return readFile(alnJsp, "/WEB-INF/classes"+ fileName);
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

}
