package aionem.net.sdk.jsp;

import aionem.net.sdk.data.AlnData;
import lombok.extern.log4j.Log4j;

import javax.servlet.jsp.PageContext;


@Log4j
public class AlnJspProperties {

    private final PageContext pageContext;
    public final String properties;
    private AlnData data;


    public AlnJspProperties(PageContext pageContext, Class<?> type) {
        this(pageContext, type.getPackageName() +"."+ type.getName());
    }
    public AlnJspProperties(PageContext pageContext, String properties) {
        this.pageContext = pageContext;
        this.properties = properties;
        init();
    }
    private void init() {
        Object properties = pageContext.getAttribute(this.properties, PageContext.REQUEST_SCOPE);
        data = new AlnData(properties);
    }

    public <T> T adapt(Class<T> type) {
        try {
            if(type.getSuperclass().isAssignableFrom(AlnData.class) || type.isAssignableFrom(AlnData.class)) {
                return type.getConstructor(this.data.getClass()).newInstance(this.data);
            }else {
                return (T) type.getConstructor();
            }
        }catch (Exception e) {
            log.error("\nERROR: DB JspProperties - Adapt ::" + e +"\n");
        }
        return null;
    }

    public static <T> T adapt(PageContext pageContext, Class<T> type) {
        AlnJspProperties alnJspProperties = new AlnJspProperties(pageContext, type);
        return alnJspProperties.adapt(type);
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

}
