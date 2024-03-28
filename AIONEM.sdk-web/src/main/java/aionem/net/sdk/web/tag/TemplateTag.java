package aionem.net.sdk.web.tag;

import aionem.net.sdk.web.WebContext;
import aionem.net.sdk.web.beans.Page;
import aionem.net.sdk.web.dao.DaoTemplate;

import javax.servlet.jsp.tagext.SimpleTagSupport;
import java.io.IOException;


public class TemplateTag extends SimpleTagSupport {


    @Override
    public void doTag() throws IOException {

        final WebContext webContext = WebContext.getInstance(getJspContext());

        final Page currentPage = webContext.getCurrentPage();

        final DaoTemplate daoTemplate = new DaoTemplate();
        daoTemplate.render(webContext, currentPage.getTemplate());

        webContext.getPageManager().cache(webContext, currentPage.isCache());
    }

}
