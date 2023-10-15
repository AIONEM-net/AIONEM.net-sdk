package aionem.net.sdk.jsp;

import aionem.net.sdk.data.AlnData;
import aionem.net.sdk.data.AlnDatas;
import aionem.net.sdk.utils.AlnUtilsText;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;

import javax.inject.Inject;
import javax.inject.Named;
import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;


@Log4j2
public @Getter abstract class AlnJspCmp {

    private AlnJsp alnJsp;
    protected AlnJspProperties properties;

    public AlnJspCmp() {
        properties = new AlnJspProperties();
    }
    public AlnJspCmp(final AlnJsp alnJsp) {
        init(this, alnJsp, new AlnJspProperties());
    }
    public AlnJspCmp(final AlnJsp alnJsp, final AlnJspProperties properties) {
        init(this, alnJsp, properties);
    }

    public <T> T init(T t, final AlnJsp alnJsp, AlnJspProperties properties) {
        this.alnJsp = alnJsp;
        boolean isNew = false;

        if(properties == null) {
            properties = new AlnJspProperties();
        }
        if(properties.isEmpty()) {
            final File file = new File(alnJsp.getRealPathCurrent(AlnJspProperties.PROPERTIES_JSON));
            final AlnData data = new AlnData(file);
            properties.init(data);
        }

        System.out.println(properties.size() +" == "+ this.properties.size() +" : "+ t);

        if(!properties.isEmpty() && !properties.equals(this.properties)) {

            for(final Field field : t.getClass().getDeclaredFields()) {
                final int modifiers = field.getModifiers();
                final boolean isStatic = Modifier.isStatic(modifiers);
                final boolean isFinal = Modifier.isFinal(modifiers);
                if(!isStatic && !isFinal) {
                    field.setAccessible(true);
                    final Inject anInject = field.isAnnotationPresent(Inject.class) ? field.getDeclaredAnnotation(Inject.class) : null;
                    if(anInject != null) {
                        final Named anNamed = field.isAnnotationPresent(Named.class) ? field.getDeclaredAnnotation(Named.class) : null;
                        final String fieldName = field.getName();
                        final String key = anNamed != null ? AlnUtilsText.notEmpty(anNamed.value(), fieldName) : fieldName;
                        try {
                            Object value = field.get(t);
                            if(properties.has(key)) {
                                value = properties.get(key, value);
                            }
                            if(value == null) {
                                value = properties.get(key);
                            }
                            field.set(t, value);
                        } catch (Exception e) {
                            log.error("\nERROR: AlnCmp - init " + e + "\n");
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

        return t;
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

    public AlnData getChild(final String key) {
        return properties.getChild(key);
    }

    public AlnDatas getChildren() {
        return properties.getChildren();
    }

    public AlnDatas getChildren(final String key) {
        return properties.getChildren(key);
    }

}
