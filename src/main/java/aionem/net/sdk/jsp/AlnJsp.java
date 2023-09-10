package aionem.net.sdk.jsp;

import aionem.net.sdk.data.AlnData;
import com.google.gson.JsonObject;
import lombok.extern.log4j.Log4j;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.PageContext;


@Log4j
public class AlnJsp {

    private final HttpServletRequest request;
    private final HttpServletResponse response;
    private final PageContext pageContext;
    public final String properties = "properties";
    public final String propertiesKey;
    private AlnData dataProperties;

    public AlnJsp(HttpServletRequest request, HttpServletResponse response, PageContext pageContext) {
        this.request = request;
        this.response = response;
        this.pageContext = pageContext;
        this.propertiesKey = properties;
    }
    public AlnJsp(HttpServletRequest request, HttpServletResponse response, PageContext pageContext, Class type) {
        this.request = request;
        this.response = response;
        this.pageContext = pageContext;
        this.propertiesKey = type.getPackageName() +"."+ type.getName();
    }

    private void init(HttpServletRequest request, HttpServletResponse response, PageContext pageContext, Class type) {

    }

    public String set(Object value) {
        set(properties, value);
        return properties;
    }
    public void set(AlnData data) {
        value(data, data.getClass());
    }
    public void set(String name, Object value) {
        pageContext.setAttribute(name, value, PageContext.REQUEST_SCOPE);
    }

    public String name(Class<?> type) {
        return type.getPackageName() +"."+ type.getName();
    }
    public String value(AlnData data, Class<?> type) {
        pageContext.setAttribute(name(type), data.toString(), PageContext.REQUEST_SCOPE);
        return data.toString();
    }

    public void noCache() {
        response.setHeader("Dispatcher", "no-cache");
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Cache-Control", "no-cache");
    }

}
