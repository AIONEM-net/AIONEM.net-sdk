package aionem.net.sdk.web.tag;

import aionem.net.sdk.web.WebContext;

import javax.servlet.jsp.tagext.SimpleTagSupport;


public class WebContextTag extends SimpleTagSupport {


    @Override
    public void doTag() {
        WebContext.getInstance(getJspContext());
    }

}
