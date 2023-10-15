package aionem.net.sdk.data;

import aionem.net.sdk.jsp.AlnJsp;
import aionem.net.sdk.jsp.AlnJspProperties;
import aionem.net.sdk.utils.AlnUtilsText;
import lombok.extern.log4j.Log4j2;

import javax.servlet.jsp.PageContext;
import java.util.Locale;
import java.util.ResourceBundle;


@Log4j2
public class AlnI18n {

    private AlnData data;
    private String baseName;
    private ResourceBundle resourceBundle;
    private Locale locale;
    private PageContext pageContext;

    public static final String folder = "i18n";

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
    public AlnI18n(final String baseName, final AlnI18n i18n) {
        init(baseName, i18n);
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
    public AlnI18n init(final String baseName, final AlnI18n i18n) {
        return init(baseName, i18n.getLocal(), new AlnData());
    }
    public AlnI18n init(final String baseName, final AlnI18n i18n, final AlnJspProperties properties) {
        return init(baseName, i18n.getLocal(), properties);
    }
    public AlnI18n init(final String baseName, final AlnI18n i18n, final PageContext pageContext) {
        this.pageContext = pageContext;
        return init(baseName, i18n);
    }
    public AlnI18n init(final String baseName, final AlnI18n i18n, final AlnJspProperties properties, final PageContext pageContext) {
        this.pageContext = pageContext;
        return init(baseName, i18n, properties);
    }
    public AlnI18n init(final String baseName, final AlnJsp alnJsp, final AlnJspProperties properties) {
        return init(baseName, alnJsp.getLocale(), properties.getData());
    }
    public AlnI18n init(final String baseName, final Locale locale, final AlnJspProperties properties) {
        return init(baseName, locale, properties.getData());
    }
    public AlnI18n init(String baseName, final Locale locale, final AlnData data) {
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
        if(AlnUtilsText.isEmpty(key)) return AlnUtilsText.notEmpty(defaultValue, "");
        String value = "";
        try {
            if(data != null && data.has(key)) {
                value = data.get(key);
            }
            if(resourceBundle != null && AlnUtilsText.isEmpty(value)) {
                if(resourceBundle.containsKey(key)) {
                    value = resourceBundle.getString(key);
                }else if(resourceBundle.containsKey(key.toLowerCase())) {
                    value = resourceBundle.getString(key.toLowerCase());
                }
            }
            if(AlnUtilsText.isEmpty(value)) {
                if(isI18n) {
                    if(pageContext != null) {
                        final Object i18n = pageContext.getAttribute("i18n", PageContext.APPLICATION_SCOPE);
                        if(i18n != null) {
                            value = ((AlnI18n) i18n).get(key, defaultValue, false);
                        }
                    }
                }
            }
        }catch(Exception e) {
            log.error("\nERROR: - get ::" + e +"\n");
        }
        if(AlnUtilsText.isEmpty(value)) {
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
