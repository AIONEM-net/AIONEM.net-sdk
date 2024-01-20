package aionem.net.sdk.web.modals;

import aionem.net.sdk.core.utils.UtilsText;
import aionem.net.sdk.web.AioWeb;
import aionem.net.sdk.web.beans.Properties;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;

import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;


@Log4j2
public @Getter abstract class Component {

    protected AioWeb aioWeb;
    protected Properties properties;
    private volatile Object instance;

    public Component() {
        properties = new Properties();
    }

    public <T> T init(final T instance) {
        this.instance = instance;
        return instance;
    }

    public <T> T init(T instance, final AioWeb aioWeb) {
        return init(instance, aioWeb, new Properties(aioWeb));
    }

    public <T> T init(final AioWeb aioWeb) {
        return (T) init(instance, aioWeb, aioWeb.getPageProperties());
    }

    public <T> T init(final AioWeb aioWeb, final Properties properties) {
        return (T) init(instance, aioWeb, properties);
    }

    public <T> T init(final HttpServletRequest request) {
        return (T) init(instance, (AioWeb) request.getAttribute("aioWeb"), new Properties(request));
    }

    public <T> T init(final T instance, final AioWeb aioWeb, final Properties properties) {
        this.instance = instance;
        this.aioWeb = aioWeb;
        boolean isNew = false;

        if(instance != null && !properties.isEmpty() && !properties.equals(this.properties)) {

            for(final Field field : instance.getClass().getDeclaredFields()) {
                final int modifiers = field.getModifiers();
                final boolean isStatic = Modifier.isStatic(modifiers);
                final boolean isFinal = Modifier.isFinal(modifiers);
                if(!isStatic && !isFinal) {
                    field.setAccessible(true);
                    final Inject anInject = field.isAnnotationPresent(Inject.class) ? field.getDeclaredAnnotation(Inject.class) : null;
                    if(anInject != null) {
                        final Named anNamed = field.isAnnotationPresent(Named.class) ? field.getDeclaredAnnotation(Named.class) : null;
                        final String fieldName = field.getName();
                        final String key = anNamed != null ? UtilsText.notEmpty(anNamed.value(), fieldName) : fieldName;
                        try {
                            Object value = field.get(instance);
                            if(properties.has(key)) {
                                value = properties.get(key, value);
                            }
                            if(value == null) {
                                value = properties.get(key);
                            }
                            field.set(instance, value);
                        }catch(final Exception e) {
                            log.error("\nERROR: Cmp - init " + e + "\n");
                        }
                    }
                }
            }

            isNew = true;

        }else if(this.properties.isEmpty()) {
            isNew = true;
        }

        this.properties = properties;
        if(isNew) {
            init();
        }

        return instance;
    }

    protected abstract void init();

    public String get(final String key) {
        return properties.get(key);
    }

    public <T> T get(final String key, final T defaultValue) {
        return this.properties.get(key, defaultValue);
    }

    public <T> T get(final String key, final Class<T> type) {
        return this.properties.get(key, type);
    }

    public Object getObject(final String key) {
        return properties.getObject(key);
    }

    public Properties getChild(final String key) {
        return properties.getChild(key);
    }

    public ArrayList<Properties> getChildren() {
        return properties.getChildren();
    }

    public ArrayList<Properties> getChildren(final String key) {
        return properties.getChildren(key);
    }

}
