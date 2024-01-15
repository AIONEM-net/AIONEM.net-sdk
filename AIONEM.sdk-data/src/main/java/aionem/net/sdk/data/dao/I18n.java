package aionem.net.sdk.data.dao;

import aionem.net.sdk.core.utils.UtilsText;
import aionem.net.sdk.data.beans.Data;
import aionem.net.sdk.data.utils.UtilsResource;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;

import java.util.HashMap;
import java.util.Locale;
import java.util.ResourceBundle;


@Log4j2
public class I18n {

    private static final HashMap<String, Data> mapData = new HashMap<>();

    @Getter
    private String name = "en";
    @Getter
    private Data data = new Data();
    private ResourceBundle resourceBundle;
    private Locale locale;

    public I18n() {
        init(name);
    }

    public I18n(final String name) {
        init(name);
    }

    public I18n(final Locale locale) {
        init(locale);
    }

    public I18n(final String name, final Locale locale) {
        init(name, locale);
    }

    public I18n(final String name, final I18n I18n1) {
        init(name, I18n1);
    }

    public I18n init(final String name) {
        return init(name, locale);
    }

    public I18n init(final Locale locale) {
        return init("", locale);
    }

    public I18n init(final String name, final I18n I18n) {
        return init(name, I18n.getLocal());
    }

    public I18n init(String name, final Locale locale) {
        try {

            if(UtilsText.isEmpty(name)) {
                name = locale != null ? locale.getLanguage() : this.name;
            }

            if(!name.equals(this.name)) {
                this.name = name;
                this.locale = locale == null ? getLocal() : locale;

                this.data = getData(name);

                this.resourceBundle = UtilsResource.getResourceBundle(name, this.locale, "/ui.config/i18n", "/i18n");
            }

        }catch(final Exception e) {
            log.error("\nERROR: - init ::" + e +"\n");
        }
        return this;
    }

    public String get(final String key) {
        return get(key, key);
    }

    public String get(final String key1, final String key2, final int size) {
        if(size == 1) {
            return get(key1);
        }else {
            return get(key2);
        }
    }

    public String get(final String key, final String defaultValue) {
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

        }catch(final Exception e) {
            log.error("\nERROR: - get ::" + e +"\n");
        }

        if(UtilsText.isEmpty(value)) value = defaultValue;

        return value;
    }

    private static <T> Data getData(String name) {
        Data data = null;

        if(!name.endsWith(".json")) name += ".json";

        if(mapData.containsKey(name)) {
            data = mapData.get(name);
        }
        if(data == null || data.isEmpty()) {

            String json = UtilsResource.readResourceOrParent(name, "/ui.config/i18n", "/i18n");
            if(UtilsText.isEmpty(json)) {
                json = UtilsResource.readResource(name, "/ui.config/i18n", "/i18n");
            }

            if(!UtilsText.isEmpty(json)) {
                data = new Data(json);
                mapData.put(name, data);
            }
        }

        return data != null ? data : new Data();
    }
    
    public Locale getLocal() {
        if(locale == null) {
            locale = Locale.getDefault();
        }
        return locale;
    }

    @Override
    public String toString() {
        return data.toString();
    }

    public void invalidate() {
        mapData.remove(getName());
    }

    public static void invalidateAll() {
        mapData.clear();
    }

}
