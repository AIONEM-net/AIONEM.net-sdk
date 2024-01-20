package aionem.net.sdk.web.tag;

import aionem.net.sdk.web.AioWeb;

import javax.servlet.jsp.tagext.SimpleTagSupport;


public class AioWebTag extends SimpleTagSupport {


    @Override
    public void doTag() {
        final AioWeb aioWeb = new AioWeb(getJspContext());
        getJspContext().setAttribute("aioWeb", aioWeb);
    }

}
