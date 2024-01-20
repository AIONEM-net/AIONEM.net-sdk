package aionem.net.sdk.web.tag;

import aionem.net.sdk.web.WebContext;

import javax.servlet.jsp.tagext.SimpleTagSupport;


public class WebContextTag extends SimpleTagSupport {


    @Override
    public void doTag() {
        final WebContext webContext = new WebContext(getJspContext());
        getJspContext().setAttribute("webContext", webContext);
    }

}
