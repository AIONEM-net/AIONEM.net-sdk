package aionem.net.sdk.jsp;

import aionem.net.sdk.data.AlnData;
import lombok.Getter;
import lombok.extern.log4j.Log4j;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.PageContext;


@Log4j
public @Getter class AlnJspProperties {

    private AlnData data;

    public AlnJspProperties() {
        data = new AlnData();
    }
    public AlnJspProperties(final HttpServletRequest request, final Class<?> type) {
        this(request, type.getPackageName() +"."+ type.getName());
    }
    public AlnJspProperties(final HttpServletRequest request, final String propertiesKey) {
        init(request, propertiesKey);
    }

    public AlnJspProperties init(final HttpServletRequest request, final Class<?> type) {
        return init(request, type.getPackageName() +"."+ type.getName());
    }
    public AlnJspProperties init(final HttpServletRequest request, final String propertiesKey) {
        final Object properties = request.getAttribute(propertiesKey);
        data = new AlnData(properties);
        return this;
    }

    public String get(final String key) {
        return data.get(key);
    }
    public Object getObject(final String key) {
        return data.get(key);
    }
    public <T> T get(final String key, final T defaultValue) {
        return this.data.get(key, defaultValue);
    }
    public <T> T get(final String key, final Class<T> type) {
        return this.data.get(key, type);
    }

    public <T> T adapt(final Class<T> type) {
        try {
            if(type.getSuperclass().isAssignableFrom(AlnData.class) || type.isAssignableFrom(AlnData.class)) {
                return type.getConstructor(this.data.getClass()).newInstance(this.data);
            }else {
                return type.getConstructor().newInstance();
            }
        }catch (Exception e) {
            log.error("\nERROR: DB JspProperties - Adapt ::" + e +"\n");
        }
        return null;
    }

    public static <T> T adapt(final HttpServletRequest request, final Class<T> type) {
        final AlnJspProperties alnJspProperties = new AlnJspProperties(request, type);
        return alnJspProperties.adapt(type);
    }

}
