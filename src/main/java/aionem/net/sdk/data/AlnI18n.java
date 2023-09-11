package aionem.net.sdk.data;

import aionem.net.sdk.jsp.AlnJspProperties;
import aionem.net.sdk.utils.AlnTextUtils;
import lombok.extern.log4j.Log4j;

import java.util.Locale;
import java.util.ResourceBundle;


@Log4j
public class AlnI18n {

    public AlnData data;

    private ResourceBundle resourceBundle;

    public AlnI18n() {

    }
    public AlnI18n(final String baseName) {
        this(baseName, getLocal(), new AlnData());
    }
    public AlnI18n(final String baseName, final Locale locale) {
        this(baseName, locale, new AlnData());
    }
    public AlnI18n(final String baseName, final AlnData data) {
        this(baseName, getLocal(), data);
    }
    public AlnI18n(final String baseName, final AlnJspProperties properties) {
        this(baseName, getLocal(), properties.getData());
    }
    public AlnI18n(final String baseName, final Locale locale, final AlnJspProperties properties) {
        this(baseName, locale, properties.getData());
    }
    public AlnI18n(final String baseName, final Locale locale, final AlnData data) {
        init(baseName, locale, data);
    }


    public AlnI18n init(final String baseName) {
        return init(baseName, getLocal(), new AlnData());
    }
    public AlnI18n init(final String baseName, final Locale locale) {
        return init(baseName, locale, new AlnData());
    }
    public AlnI18n init(final String baseName, final AlnData data) {
        return init(baseName, getLocal(), data);
    }
    public AlnI18n init(final String baseName, final AlnJspProperties properties) {
        return init(baseName, getLocal(), properties.getData());
    }
    public AlnI18n init(final String baseName, final Locale locale, final AlnJspProperties properties) {
        return init(baseName, locale, properties.getData());
    }
    public AlnI18n init(final String baseName, final Locale locale, final AlnData data) {
        this.data = data;
        try {
            if (locale != null) {
                this.resourceBundle = ResourceBundle.getBundle(baseName, locale);
            } else {
                this.resourceBundle = ResourceBundle.getBundle(baseName);
            }
        }catch(Exception e) {
            log.error("\nERROR: - init ::" + e +"\n");
        }
        return this;
    }

    public String get(final String key) {
        String value = "";
        if(data != null) {
            value = data.get(key);
        }
        if(resourceBundle != null && AlnTextUtils.isEmpty(value)) {
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
