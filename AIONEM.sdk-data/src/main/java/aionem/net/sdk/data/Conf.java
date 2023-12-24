package aionem.net.sdk.data;

import aionem.net.sdk.core.utils.UtilsText;
import lombok.extern.log4j.Log4j2;

import java.util.ResourceBundle;


@Log4j2
public class Conf {

    private Data data;
    private ResourceBundle resourceBundle;

    public Data getData() {
        if(data == null) {
            data = new Data(getClass().getClassLoader().getResourceAsStream("application.json"));
        }
        return data;
    }

    public ResourceBundle getResourceBundle() {
        if(resourceBundle == null) {
            resourceBundle = ResourceBundle.getBundle("application");
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
        String value = null;
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
        return value;
    }

}
