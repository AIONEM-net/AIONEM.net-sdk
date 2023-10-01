package aionem.net.sdk.utils;

import aionem.net.sdk.jsp.AlnJsp;
import lombok.extern.log4j.Log4j2;

import java.io.InputStream;


@Log4j2
public class AlnJspUtils {

    public static String readFile(final AlnJsp alnJsp, final String fileName) {
        try {
            final InputStream inputStream = alnJsp.getResourceAsStream(fileName);
            if(inputStream != null) {
                return AlnTextUtils.toString(inputStream);
            }
        }catch(Exception e) {
            log.error("\nERROR: - readFile ::" + e +"\n");
        }
        return null;
    }

}
