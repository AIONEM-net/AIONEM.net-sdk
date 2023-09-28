package aionem.net.sdk.data;

import aionem.net.sdk.jsp.AlnJspProperties;
import aionem.net.sdk.utils.AlnTextUtils;
import lombok.extern.log4j.Log4j;

import java.util.Locale;
import java.util.ResourceBundle;


@Log4j
public class AlnI18n {

    private AlnData data;

    private String baseName;
    private ResourceBundle resourceBundle;
    private Locale locale;

    public AlnI18n() {

    }
    public AlnI18n(final String baseName) {
        this(baseName, null, new AlnData());
    }
    public AlnI18n(final String baseName, final Locale locale) {
        this(baseName, locale, new AlnData());
    }
    public AlnI18n(final String baseName, final AlnData data) {
        this(baseName, null, data);
    }
    public AlnI18n(final String baseName, final AlnJspProperties properties) {
        this(baseName, null, properties.getData());
    }
    public AlnI18n(final String baseName, final Locale locale, final AlnJspProperties properties) {
        this(baseName, locale, properties.getData());
    }
    public AlnI18n(final String baseName, final Locale locale, final AlnData data) {
        init(baseName, locale, data);
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
    public AlnI18n init(String baseName, final Locale locale, final AlnData data) {
        try {
            this.data = data;
            if(!baseName.startsWith("i18n/")) {
                baseName = "i18n/" + baseName;
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
        if(AlnTextUtils.isEmpty(key)) return "";
        String value = "";
        if(data != null) {
            value = data.get(key);
        }
        try {
            if(resourceBundle != null && AlnTextUtils.isEmpty(value)) {
                value = resourceBundle.getString(key);
            }
        }catch(Exception e) {
            log.error("\nERROR: - get ::" + e +"\n");
        }
        if(AlnTextUtils.isEmpty(value)) {
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
