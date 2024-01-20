package aionem.net.sdk.web.system.servlets;

import aionem.net.sdk.core.utils.UtilsText;
import aionem.net.sdk.data.utils.UtilsResource;
import aionem.net.sdk.web.WebContext;
import aionem.net.sdk.web.dao.DaoWebAuth;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;


@WebServlet(value={"/ui.system/*"}, name="ServletSysUi", description="Ui.System • AIONEM.net - Ui Servlet")
public class ServletSysUi extends HttpServlet {


    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        final WebContext webContext = new WebContext(request, response);

        String path = request.getPathInfo();
        if(UtilsText.isEmpty(path)) {
            path = "home";
        }

        final DaoWebAuth daoWebAuth = DaoWebAuth.getInstance();

        if(!daoWebAuth.isAuthenticated(webContext)) {
            if(!"/login".equalsIgnoreCase(path)) {
                webContext.sendRedirect(UtilsResource.path(request.getServletPath(), "login"), true);
                return;
            }
        }else {
            if("/login".equalsIgnoreCase(path)) {
                webContext.sendRedirect(request.getServletPath());
            }
        }

        final String page = UtilsResource.path("/ui.system", path);
        final String title = UtilsText.capitalizeFirstLetter(page.substring(page.lastIndexOf("/")+1));

        final String pageUi = "/ui.system/ui";
        final String pageError = "/ui.system/error";

        String body = UtilsResource.readResource(".html", page);
        String css = UtilsResource.readResource(".css", pageUi);
        String js = UtilsResource.readResource(".js", pageUi);

        if(!UtilsText.isEmpty(body)) {
            css += UtilsResource.readResource(".css", page);
            js += UtilsResource.readResource(".js", page);
        }else {
            body = UtilsResource.readResource(".html", pageError);
            css += UtilsResource.readResource(".css", pageError);
            js += UtilsResource.readResource(".js", pageError);
        }

        body = UtilsResource.readResource(".html", pageUi) + body;

        response.setContentType("text/html;charset=UTF-8");
        final PrintWriter out = response.getWriter();

        out.println("<html>");
        out.println("<head>");
        out.println("<title>"+ title +" • AIONEM.net</title>");
        out.println("<style>");
        out.println(UtilsText.notNull(css));
        out.println("</style>");
        out.println("</head>");
        out.println("<body class='aionem-net-ui-system'>");

        out.println(UtilsText.notNull(body));

        out.println("<script>");
        out.println(UtilsText.notNull(js));
        out.println("</script>");
        out.println("</body>");
        out.println("</html>");
        out.close();
    }

}
