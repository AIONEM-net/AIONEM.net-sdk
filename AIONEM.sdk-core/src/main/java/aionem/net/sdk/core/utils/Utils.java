package aionem.net.sdk.core.utils;

import lombok.extern.log4j.Log4j2;

import java.util.*;


@Log4j2
public class Utils {


    public static int random(final int high, final int low) {
        return new Random().nextInt(high - low) + low;
    }

    public static String getUUID() {
        return UtilsText.toString(UUID.randomUUID());
    }

}
