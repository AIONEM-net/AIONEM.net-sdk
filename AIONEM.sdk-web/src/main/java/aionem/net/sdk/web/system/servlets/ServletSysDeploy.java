package aionem.net.sdk.web.system.servlets;

import aionem.net.sdk.web.servlets.GetMapping;
import aionem.net.sdk.web.servlets.HttpServletApi;
import aionem.net.sdk.web.system.dao.DaoSysDeploy;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;


@WebServlet(value={"/api/ui.system/deploy/*"}, name="ServletSysDeploy", description="Ui.System • AIONEM.net - Deploy Servlet")
public class ServletSysDeploy extends HttpServletApi {


    @GetMapping("/*")
    protected void doGetDeploy(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {

        final boolean isDeployWar = DaoSysDeploy.deployWar("prod", "aionem.net.war");

        response.setContentType("text/html;charset=UTF-8");
        final PrintWriter out = response.getWriter();

        out.println("<html>");
        out.println("<head>");
        out.println("<title>Ui.System • AIONEM.net</title>");
        out.println("</head>");
        out.println("<body>");

        out.println("<br>" +"DeployWar PROD: "+ isDeployWar);

        out.println("</body>");
        out.println("</html>");
        out.close();
    }

}
