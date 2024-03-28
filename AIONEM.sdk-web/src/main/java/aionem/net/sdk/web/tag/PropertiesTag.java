package aionem.net.sdk.web.tag;

import aionem.net.sdk.web.beans.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.SimpleTagSupport;


public class PropertiesTag extends SimpleTagSupport {


    @Override
    public void doTag() {
        final PageContext pageContext = (PageContext) getJspContext();
        final HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();
        final Properties properties = new Properties(request);
        getJspContext().setAttribute("properties", properties);
    }

}
