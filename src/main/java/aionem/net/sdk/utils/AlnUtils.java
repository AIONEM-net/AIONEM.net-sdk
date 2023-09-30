package aionem.net.sdk.utils;

import lombok.extern.log4j.Log4j2;

import java.util.*;


@Log4j2
public class AlnUtils {


    public static int random(final int high, final int low) {
        return new Random().nextInt(high - low) + low;
    }

    public static String getUUID() {
        return String.valueOf(UUID.randomUUID());
    }

}
