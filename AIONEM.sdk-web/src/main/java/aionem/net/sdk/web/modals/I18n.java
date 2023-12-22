package aionem.net.sdk.web.modals;

import aionem.net.sdk.data.Data;
import aionem.net.sdk.core.utils.UtilsText;
import aionem.net.sdk.web.AioWeb;
import lombok.extern.log4j.Log4j2;

import java.util.Locale;
import java.util.ResourceBundle;


@Log4j2
public class I18n {

    private Data data;
    private String baseName;
    private ResourceBundle resourceBundle;
    private Locale locale;
    private AioWeb aioWeb;

    public static final String folder = "i18n";

    public I18n() {

    }
    public I18n(final String baseName) {
        this(baseName, null, new Data());
    }
    public I18n(final String baseName, final Locale locale) {
        this(baseName, locale, new Data());
    }
    public I18n(final String baseName, final Data data) {
        this(baseName, null, data);
    }
    public I18n(final String baseName, final Properties properties) {
        this(baseName, null, properties.getData());
    }
    public I18n(final String baseName, final I18n i18n) {
        init(baseName, i18n);
    }
    public I18n(final String baseName, final Locale locale, final Properties properties) {
        this(baseName, locale, properties.getData());
    }
    public I18n(final String baseName, final Locale locale, final Data data) {
        init(baseName, locale, data);
    }

    public I18n init(final String baseName, final Locale locale) {
        return init(baseName, locale, new Data());
    }
    public I18n init(final String baseName, final Data data) {
        return init(baseName, getLocal(), data);
    }
    public I18n init(final String baseName, final Properties properties) {
        return init(baseName, getLocal(), properties.getData());
    }
    public I18n init(final String baseName, final I18n i18n) {
        return init(baseName, i18n.getLocal(), new Data());
    }
    public I18n init(final String baseName, final I18n i18n, final Properties properties) {
        return init(baseName, i18n.getLocal(), properties);
    }
    public I18n init(final String baseName, final I18n i18n, final AioWeb aioWeb) {
        this.aioWeb = aioWeb;
        return init(baseName, i18n);
    }
    public I18n init(final String baseName, final I18n i18n, final Properties properties, final AioWeb aioWeb) {
        this.aioWeb = aioWeb;
        return init(baseName, i18n, properties);
    }
    public I18n init(final String baseName, final AioWeb aioWeb, final Properties properties) {
        return init(baseName, aioWeb.getLocale(), properties.getData());
    }
    public I18n init(final String baseName, final Locale locale, final Properties properties) {
        return init(baseName, locale, properties.getData());
    }
    public I18n init(String baseName, final Locale locale, final Data data) {
        try {
            this.data = data;
            if(!baseName.startsWith("/"+folder+"/") && !baseName.startsWith(folder+"/")) {
                baseName = "/"+folder+"/" + baseName;
            }
            if(this.locale != locale || !this.baseName.equals(baseName)) {
                this.baseName = baseName;
                this.locale = locale != null ? locale : getLocal();
                this.resourceBundle = ResourceBundle.getBundle(this.baseName, this.locale);
            }
        }catch(Exception e) {
            log.error("\nERROR: - init ::" + e +"\n");
        }
        return this;
    }

    public String get(final String key) {
        return get(key, key);
    }
    public String get(final String key, final String defaultValue) {
        return get(key, defaultValue, true);
    }
    public String get(final String key, final boolean isI18n) {
        return get(key, key, isI18n);
    }
    public String get(final String key, final String defaultValue, final boolean isI18n) {
        if(UtilsText.isEmpty(key)) return UtilsText.notEmpty(defaultValue, "");
        String value = "";
        try {
            if(data != null && data.has(key)) {
                value = data.get(key);
            }
            if(resourceBundle != null && UtilsText.isEmpty(value)) {
                if(resourceBundle.containsKey(key)) {
                    value = resourceBundle.getString(key);
                }else if(resourceBundle.containsKey(key.toLowerCase())) {
                    value = resourceBundle.getString(key.toLowerCase());
                }
            }
            if(UtilsText.isEmpty(value)) {
                if(isI18n) {
                    if(aioWeb != null) {
                        final Object i18n = aioWeb.getApplicationAttribute("i18n");
                        if(i18n != null) {
                            value = ((I18n) i18n).get(key, defaultValue, false);
                        }
                    }
                }
            }
        }catch(Exception e) {
            log.error("\nERROR: - get ::" + e +"\n");
        }
        if(UtilsText.isEmpty(value)) {
            value = defaultValue;
        }
        return value;
    }

    public String get(final String key1, final String key2, final int size) {
        if(size == 1) {
            return get(key1);
        }else {
            return get(key2);
        }
    }

    private Locale getLocal() {
        if(locale == null) {
            locale = Locale.getDefault();
        }
        return locale;
    }

}
