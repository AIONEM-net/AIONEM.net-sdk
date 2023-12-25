package aionem.net.sdk.data;

import aionem.net.sdk.core.utils.UtilsText;
import aionem.net.sdk.data.utils.UtilsResource;
import lombok.extern.log4j.Log4j2;

import java.util.ResourceBundle;


@Log4j2
public class ConfApp {

    private final String env;
    private Data data;
    private ResourceBundle resourceBundle;

    private ConfApp() {
        this("");
    }

    private static ConfApp confApp;
    public static ConfApp getInstance() {
        if(confApp == null) {
            confApp = new ConfApp();
        }
        return confApp;
    }

    public ConfApp(final String env) {
        this.env = UtilsText.notNull(env);
    }

    private Data getData() {
        if(data == null) {
            data = new Data(UtilsResource.getParentResourceAsStream(this.getClass(), "application.json",
                    "ui.config/env/"+env, "config/env/"+env, "config/", "")
            );
        }
        return data;
    }

    private ResourceBundle getResourceBundle() {
        if(resourceBundle == null) {
            resourceBundle = UtilsResource.getResourceBundle("application",
                    "ui.config/env/"+env, "config/env/"+env, "config/", ""
            );
        }
        return resourceBundle;
    }

    public String get(final String key) {
        return get(key, null, "");
    }

    public String get(final String key, final String defaultValue) {
        return get(key, null, defaultValue);
    }

    public String getOr(final String key1, final String key2) {
        return get(key1, key2, "");
    }

    public String get(final String key1, final String key2, final String defaultValue) {
        String value = "";
        try {
            if(!UtilsText.isEmpty(key1)) {
                if(getData().has(key1)) {
                    value = getData().get(key1);
                }else if(getResourceBundle().containsKey(key1)) {
                    value = getResourceBundle().getString(key1);
                }
            }

            if(UtilsText.isEmpty(value) && !UtilsText.isEmpty(key2)) {
                if(getData().has(key2)) {
                    value = getData().get(key2);
                }else if(getResourceBundle().containsKey(key2)) {
                    value = getResourceBundle().getString(key2);
                }
            }
        }catch (Exception e) {
            log.error("Error getting resource bundle {}", e.getMessage());
            value = defaultValue;
        }
        return UtilsText.notNull(value);
    }

}
