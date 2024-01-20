package aionem.net.sdk.web.dao;

import aionem.net.sdk.core.utils.UtilsText;
import aionem.net.sdk.data.utils.UtilsResource;
import aionem.net.sdk.web.WebContext;
import aionem.net.sdk.web.beans.Page;
import aionem.net.sdk.web.beans.Properties;
import aionem.net.sdk.web.beans.Resource;
import aionem.net.sdk.web.config.ConfEnv;
import lombok.extern.log4j.Log4j2;

import javax.servlet.ServletException;
import javax.servlet.jsp.JspContext;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import java.io.IOException;


@Log4j2
public class DaoTemplate {


    public void render(final JspContext jspContext, final String template) throws IOException {

        final WebContext webContext = new WebContext(jspContext);
        final JspWriter out = webContext.getOut();

        final String pathTemplate = UtilsResource.path("/WEB-INF/ui.template", template);
        final Resource resourceTemplate = new Resource(pathTemplate);
        final Properties propertiesTemplate = new Properties(resourceTemplate);

        webContext.getResponse().setContentType("text/html;charset=UTF-8");

        out.println("<html lang='"+ webContext.getLanguage() +"'>");
        out.println("<head>");
        out.println("<base href='/'>");
        out.println("<title>"+ webContext.getCurrentPage().getFullTitle(webContext.getHomePage()) +"</title>");
        out.println("<meta name='template' content='"+ webContext.getCurrentPage().getTemplate() +"'>");
        out.println("<meta name='thumbnail' content='"+ webContext.getCurrentPage().getThumbnail() +"'>");
        out.println("<meta name='viewport' content='width=device-width, initial-scale=1'>");
        out.println("<meta charset='UTF-8'>");
        out.println("<meta http-equiv='Content-Type' content='text/html; charset=UTF-8'>");
        out.println("<meta property='og:url' content='"+ webContext.getCurrentPage().getUrl() +"'>");
        out.println("<meta property='og:title' content='"+ webContext.getCurrentPage().getFullTitle(webContext.getHomePage()) +"'>");
        out.println("<meta property='og:image' content='"+ webContext.getCurrentPage().getThumbnail() +"'>");
        out.println("<meta property='og:description' content='"+ webContext.getCurrentPage().getDescription() +"'>");
        out.println("<meta name='description' content='"+ webContext.getCurrentPage().getDescription() +"'>");
        out.println("<meta name='keywords' content='"+ webContext.getCurrentPage().getKeywords() +"'>");

        if(!UtilsText.isEmpty(webContext.getCurrentPage().getRedirect())) {
            out.println("<script>window.location.replace('"+ webContext.getRedirect(webContext.getCurrentPage().getRedirect()) +"');</script>");
        }

        if(!"page".equalsIgnoreCase(template)) {
            webContext.includeCatch("/WEB-INF/ui.template/page/head.jsp");
        }
        webContext.includeCatch("/ui.frontend/seo.html");
        webContext.includeCatch(UtilsResource.path(pathTemplate, "head.jsp"));
        out.println(printFrontendCss(webContext.getPageContext()));
        out.println("</head>");

        out.println("<body class='"+ propertiesTemplate.get("class") +"'>");

        for(final Properties properties : propertiesTemplate.getChildren("body")) {
            if("/WEB-INF/ui.apps/container/.jsp".equals(properties.getResourceType())) {
                includePageContents(webContext.getPageContext());
            }else {
                webContext.includeCatch(properties.getResourceType());
            }
        }

        webContext.includeCatch(UtilsResource.path(pathTemplate, "foot.jsp"));
        if(!"page".equalsIgnoreCase(template)) {
            webContext.includeCatch("/WEB-INF/ui.template/page/foot.jsp");
        }
        out.println(printFrontendJs(webContext.getPageContext()));
        out.println("</body>");
        out.println("</html>");
    }

    public void includePageContents(final PageContext pageContext) throws IOException {

        final WebContext webContext = new WebContext(pageContext);
        final JspWriter out = webContext.getOut();

        out.println("<div class='body-content'>");

        final Page currentPage = webContext.getCurrentPage();
        for(int i = 0; i < currentPage.getContents().size(); i++) {
            final Properties content = currentPage.getContents().get(i);
            final String resourceType = content.getResourceType();
            final String jsonData = content.toData().toJson().toString();

            webContext.setRequestAttribute("$_properties", jsonData);

            try {
                webContext.include(resourceType);
            } catch (ServletException | IOException e) {
                log.error("resourceType not found: {}", resourceType);
            }
        }

        out.println("</div>");

    }

    public String printFrontendCss(final PageContext pageContext) throws IOException {

        final WebContext webContext = new WebContext(pageContext);
        final JspWriter out = webContext.getOut();

        final StringBuilder styles = new StringBuilder();

        final Resource resourceTemplate = new Resource("/WEB-INF/ui.template", webContext.getCurrentPage().getTemplate());
        final Properties propertiesTemplate = resourceTemplate.getProperties();

        for(final String uiFrontend : propertiesTemplate.getArray("ui.frontend")) {

            if(!ConfEnv.getInstance().isEnvLocalOrNone()) {
                styles.append("<link rel='stylesheet' href='").append(UtilsResource.path("/ui.frontend", uiFrontend, ".css")).append("'/>");
            }else {

                final Resource resourceFrontend = new Resource(UtilsResource.path("/ui.frontend", uiFrontend));
                final Properties propertiesFrontend = resourceFrontend.getProperties();

                for(final String css : propertiesFrontend.getArray("css")) {
                    styles.append("<link rel='stylesheet' href='").append(UtilsResource.path("/ui.frontend", uiFrontend, "css", css)).append("'/>");
                }
            }

        }

        if(false) {
            out.println(styles.toString());
        }
        return styles.toString();
    }

    public String printFrontendJs(final PageContext pageContext) throws IOException {

        final StringBuilder scrips = new StringBuilder();

        final WebContext webContext = new WebContext(pageContext);
        final JspWriter out = webContext.getOut();

        final Resource resourceTemplate = new Resource("/WEB-INF/ui.template", webContext.getCurrentPage().getTemplate());
        final Properties propertiesTemplate = resourceTemplate.getProperties();

        for(final String uiFrontend : propertiesTemplate.getArray("ui.frontend")) {

            if(!ConfEnv.getInstance().isEnvLocalOrNone()) {
                scrips.append("\n")
                        .append("<script type='text/javascript' src='")
                        .append(UtilsResource.path("/ui.frontend", uiFrontend, ".js"))
                        .append("'></script>");
            }else {

                final Resource resourceFrontend = new Resource(UtilsResource.path("/ui.frontend", uiFrontend));
                final Properties propertiesFrontend = resourceFrontend.getProperties();

                for(final String js : propertiesFrontend.getArray("js")) {
                    scrips.append("\n")
                            .append("<script type='text/javascript' src='")
                            .append(UtilsResource.path("/ui.frontend", uiFrontend, "js", js))
                            .append("'></script>");
                }
            }

        }

        if(false) {
            out.println(scrips.toString());
        }
        return scrips.toString();
    }

}
