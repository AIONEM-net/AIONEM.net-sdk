package aionem.net.sdk.web.tag;

import aionem.net.sdk.web.dao.DaoTemplate;

import javax.servlet.jsp.tagext.SimpleTagSupport;
import java.io.IOException;


public class TemplateTag extends SimpleTagSupport {

    private String name;

    @Override
    public void doTag() throws IOException {

        final DaoTemplate daoTemplate = new DaoTemplate();

        daoTemplate.render(getJspContext(), name);
    }

    public void setName(final String name) {
        this.name = name;
    }

}
