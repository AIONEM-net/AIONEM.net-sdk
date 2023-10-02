package aionem.net.sdk.jsp;

import aionem.net.sdk.data.AlnData;
import aionem.net.sdk.data.AlnDatas;
import aionem.net.sdk.utils.AlnTextUtils;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;

import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.PageContext;
import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;


@Log4j2
public @Getter abstract class AlnCmp extends AlnJsp {

    protected AlnJspProperties properties;

    public AlnCmp() {
        properties = new AlnJspProperties();
    }
    public AlnCmp(final HttpServletRequest request, final HttpServletResponse response) {
        init(this, request, response, new AlnJspProperties());
    }
    public AlnCmp(final HttpServletRequest request, final HttpServletResponse response, final AlnJspProperties properties) {
        init(this, request, response, properties);
    }


    public AlnCmp init(final HttpServletRequest request, final HttpServletResponse response) {
        return init(request, response, new AlnJspProperties());
    }
    public AlnCmp init(final HttpServletRequest request, final HttpServletResponse response, final AlnJspProperties properties) {
        return init(this, request, response, null, properties);
    }
    public <T> T init(final T t, final HttpServletRequest request, final HttpServletResponse response, final AlnJspProperties properties) {
        return init(t, request, response, null, properties);
    }
    public <T> T init(final T t, final HttpServletRequest request, final HttpServletResponse response, final PageContext pageContext, AlnJspProperties properties) {
        super.init(request, response, pageContext);
        if(properties == null) {
            properties = new AlnJspProperties();
        }
        if(properties.isEmpty()) {
            final File file = new File(getRealPathCurrent(AlnJspProperties.PROPERTIES_JSON));
            final AlnData data = new AlnData(file);
            properties.init(data);
        }

        if(!properties.isEmpty() && !properties.equals(this.properties)) {

            System.out.println(properties.size() +" == "+ this.properties.size() +" : "+ t);

            for (final Field field : t.getClass().getDeclaredFields()) {
                final int modifiers = field.getModifiers();
                final boolean isStatic = Modifier.isStatic(modifiers);
                final boolean isFinal = Modifier.isFinal(modifiers);
                if (!isStatic && !isFinal) {
                    field.setAccessible(true);
                    final Inject anInject = field.isAnnotationPresent(Inject.class) ? field.getDeclaredAnnotation(Inject.class) : null;
                    if (anInject != null) {
                        final Named anNamed = field.isAnnotationPresent(Named.class) ? field.getDeclaredAnnotation(Named.class) : null;
                        final String fieldName = field.getName();
                        final String key = anNamed != null ? AlnTextUtils.notEmpty(anNamed.value(), fieldName) : fieldName;
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

            init();

        }else if(this.properties.isEmpty()) {
            init();
        }

        this.properties = properties;
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
