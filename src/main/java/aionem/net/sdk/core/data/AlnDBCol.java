package aionem.net.sdk.core.data;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;


@Retention(RetentionPolicy.RUNTIME)
public @interface AlnDBCol {

    String name();

}
