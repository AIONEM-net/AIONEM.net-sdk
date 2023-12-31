package aionem.net.sdk.web.modals;

import aionem.net.sdk.data.Data;
import aionem.net.sdk.data.Datas;
import aionem.net.sdk.core.utils.UtilsText;
import aionem.net.sdk.web.AioWeb;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;

import javax.inject.Inject;
import javax.inject.Named;
import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;


@Log4j2
public @Getter abstract class Component {

    private AioWeb aioWeb;
    protected Properties properties;
    private volatile Object instance;

    public Component() {
        properties = new Properties();
    }

    protected Component(final Object instance, final AioWeb aioWeb) {
        init(instance, aioWeb, new Properties());
    }

    public Component(final AioWeb aioWeb, final Properties properties) {
        init(this, aioWeb, properties);
    }

    public <T> T init(T instance) {
        this.instance = instance;
        return instance;
    }

    public <T> T init(final AioWeb aioWeb) {
        return (T) init(instance, aioWeb, new Properties());
    }

    public <T> T init(T instance, final AioWeb aioWeb) {
        return init(instance, aioWeb, new Properties());
    }

    public <T> T init(final AioWeb aioWeb, Properties properties) {
        return (T) init(instance, aioWeb, properties);
    }

    public <T> T init(T instance, final AioWeb aioWeb, Properties properties) {
        this.instance = instance;
        this.aioWeb = aioWeb;
        boolean isNew = false;

        if(properties == null || properties.isEmpty()) {
            properties = new Properties(aioWeb);
        }

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
                        } catch (Exception e) {
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
        return properties.get(key);
    }

    public Data getChild(final String key) {
        return properties.getChild(key);
    }

    public Datas getChildren() {
        return properties.getChildren();
    }

    public Datas getChildren(final String key) {
        return properties.getChildren(key);
    }

}
