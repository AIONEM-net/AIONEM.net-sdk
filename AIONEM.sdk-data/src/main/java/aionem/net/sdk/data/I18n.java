package aionem.net.sdk.data;

import aionem.net.sdk.core.utils.UtilsText;
import aionem.net.sdk.data.utils.UtilsResource;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;

import java.util.Locale;
import java.util.ResourceBundle;


@Log4j2
public class I18n {

    public static final String DEFAULT_LANGUAGE = "en";

    @Getter
    private String baseName;
    @Getter
    private Data data;

    private ResourceBundle resourceBundle;
    private Locale locale;

    public I18n() {
        init("");
    }

    public I18n(final String baseName) {
        init(baseName);
    }

    public I18n(final Locale locale) {
        init(locale);
    }

    public I18n(final String baseName, final Locale locale) {
        init(baseName, locale);
    }

    public I18n(final String baseName, final I18n I18n1) {
        init(baseName, I18n1);
    }

    public I18n init(final String baseName) {
        return init(baseName, locale);
    }

    public I18n init(final Locale locale) {
        return init(baseName, locale);
    }

    public I18n init(final String baseName, final I18n I18n1) {
        return init(baseName, I18n1.getLocal());
    }

    public I18n init(String baseName, final Locale locale) {
        try {

            if(UtilsText.isEmpty(baseName)) {
                baseName = locale != null ? locale.getLanguage() : DEFAULT_LANGUAGE;
            }

            if(!baseName.equals(this.baseName)) {
                this.baseName = baseName;
                this.locale = locale == null ? getLocal() : locale;

                final String json = UtilsResource.readParentResource(this.getClass(), baseName + ".json", "/ui.config/i18n", "/i18n");
                this.data = new Data(json);

                this.resourceBundle = UtilsResource.getResourceBundle(baseName, this.locale, "/ui.config/i18n", "/i18n");
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

    public String get(final String key1, final String key2, final int size) {
        if(size == 1) {
            return get(key1);
        }else {
            return get(key2);
        }
    }

    public String get(final String key, final boolean isI18n1) {
        return get(key, key, isI18n1);
    }

    public String get(final String key, final String defaultValue, final boolean isI18n1) {
        if(UtilsText.isEmpty(key)) return UtilsText.notEmpty(defaultValue, "");

        String value = "";

        try {

            if(data != null) {
                value = data.get(key);
                if(data.has(key)) {
                    value = data.get(key);
                }else if(data.has(key.toLowerCase())) {
                    value = data.get(key.toLowerCase());
                }else if(data.has(key.replace(" ", "_").toLowerCase())) {
                    value = data.get(key.replace(" ", "_").toLowerCase());
                }else if(data.has(key.replace("_", " ").toLowerCase())) {
                    value = data.get(key.replace("_", " ").toLowerCase());
                }
            }

            if(UtilsText.isEmpty(value) && resourceBundle != null) {
                if(resourceBundle.containsKey(key)) {
                    value = resourceBundle.getString(key);
                }else if(resourceBundle.containsKey(key.toLowerCase())) {
                    value = resourceBundle.getString(key.toLowerCase());
                }else if(resourceBundle.containsKey(key.replace(" ", "_").toLowerCase())) {
                    value = resourceBundle.getString(key.replace(" ", "_").toLowerCase());
                }else if(resourceBundle.containsKey(key.replace("_", " ").toLowerCase())) {
                    value = resourceBundle.getString(key.replace("_", " ").toLowerCase());
                }
            }

        }catch(Exception e) {
            log.error("\nERROR: - get ::" + e +"\n");
        }

        if(UtilsText.isEmpty(value)) value = defaultValue;

        return value;
    }

    public Locale getLocal() {
        if(locale == null) {
            locale = Locale.getDefault();
        }
        return locale;
    }

}