package aionem.net.sdk.jsp;

import aionem.net.sdk.data.AlnData;
import lombok.Getter;
import lombok.extern.log4j.Log4j;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.jsp.PageContext;


@Log4j
public @Getter class AlnJsp {

    private HttpServletRequest request;
    private HttpServletResponse response;
    private HttpSession httpSession;
    public String properties = "properties";
    public String propertiesKey;

    public AlnJsp() {
        this.propertiesKey = properties;
    }
    public AlnJsp(HttpServletRequest request, HttpServletResponse response) {
        init(request, response);
    }
    public AlnJsp(HttpServletRequest request, HttpServletResponse response, Class<?> type) {
        init(request, response, type);
    }

    public AlnJsp init(HttpServletRequest request, HttpServletResponse response) {
        return init(request, response, properties);
    }
    public AlnJsp init(HttpServletRequest request, HttpServletResponse response, Class<?> type) {
        return init(request, response, name(type));
    }
    public AlnJsp init(HttpServletRequest request, HttpServletResponse response, String propertiesKey) {
        this.request = request;
        this.response = response;
        this.httpSession = request.getSession(true);
        this.propertiesKey = propertiesKey;
        return this;
    }

    public void set(Object value) {
        set(properties, value);
    }
    public void set(AlnData data) {
        set(name(data.getClass()), data.toString());
    }
    public void set(String name, Object value) {
        request.setAttribute(name, value);
    }

    public String name(Class<?> type) {
        return type.getPackageName() +"."+ type.getName();
    }
    public String value(AlnCmp alnCmp, Class<?> type) {
        return value(alnCmp.getProperties(), type);
    }
    public String value(AlnJspProperties properties, Class<?> type) {
        return value(properties.getData(), type);
    }
    public String value(AlnData data, Class<?> type) {
        request.setAttribute(name(type), data.toString());
        return data.toString();
    }

    public void noCache() {
        response.setHeader("Dispatcher", "no-cache");
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Cache-Control", "no-cache");
    }

}
