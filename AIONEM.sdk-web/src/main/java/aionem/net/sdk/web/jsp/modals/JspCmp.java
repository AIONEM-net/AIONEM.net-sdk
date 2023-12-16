package aionem.net.sdk.web.jsp.modals;

import aionem.net.sdk.data.Data;
import aionem.net.sdk.data.Datas;
import aionem.net.sdk.core.utils.UtilsText;
import aionem.net.sdk.web.jsp.AioJsp;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;

import javax.inject.Inject;
import javax.inject.Named;
import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;


@Log4j2
public @Getter abstract class JspCmp {

    private AioJsp aioJsp;
    protected JspProperties properties;

    public JspCmp() {
        properties = new JspProperties();
    }
    public JspCmp(final AioJsp aioJsp) {
        init(this, aioJsp, new JspProperties());
    }
    public JspCmp(final AioJsp aioJsp, final JspProperties properties) {
        init(this, aioJsp, properties);
    }

    public <T> T init(T t, final AioJsp aioJsp, JspProperties properties) {
        this.aioJsp = aioJsp;
        boolean isNew = false;

        if(properties == null) {
            properties = new JspProperties();
        }
        if(properties.isEmpty()) {
            final File file = new File(aioJsp.getRealPathCurrent(JspProperties.PROPERTIES_JSON));
            final Data data = new Data(file);
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
                        final String key = anNamed != null ? UtilsText.notEmpty(anNamed.value(), fieldName) : fieldName;
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
