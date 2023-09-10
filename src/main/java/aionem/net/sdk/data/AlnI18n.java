package aionem.net.sdk.data;

import aionem.net.sdk.utils.AlnTextUtils;

import java.util.Locale;
import java.util.ResourceBundle;


public class AlnI18n {

    private final AlnData data;

    private final ResourceBundle resourceBundle;

    public AlnI18n(final String baseName) {
        this(baseName, getLocal(), new AlnData());
    }
    public AlnI18n(final String baseName, final Locale locale) {
        this(baseName, locale, new AlnData());
    }
    public AlnI18n(final String baseName, final Locale locale, final AlnData data) {
        this.data = data;
        if(locale != null) {
            this.resourceBundle = ResourceBundle.getBundle(baseName, locale);
        }else {
            this.resourceBundle = ResourceBundle.getBundle(baseName);
        }
    }

    public String get(final String key) {
        String value = data.get(key);
        if(AlnTextUtils.isEmpty(value)) {
            value = resourceBundle.getString(key);
        }
        if(AlnTextUtils.isEmpty(value)) {
            value = key;
        }
        return value;
    }

    private static Locale getLocal() {
        return null;
    }

    public static void saveLocale(final Locale locale) {

    }

}
