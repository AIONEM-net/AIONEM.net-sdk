package aionem.net.sdk.web.system.ui;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;


@WebServlet(value={"/ui.system/test"}, name="ServletSysUi", description="Ui.System • AIONEM.net - Ui Servlet")
public class ServletSysUi extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        response.setContentType("text/html;charset=UTF-8");
        final PrintWriter out = response.getWriter();

        out.println("<html>");
        out.println("<head>");
        out.println("<title>Ui.System • AIONEM.net</title>");
        out.println("</head>");
        out.println("<body>");

        out.println("</body>");
        out.println("</html>");
        out.close();
    }

}
