package aionem.net.sdk.web.jsp.map;

import aionem.net.sdk.web.jsp.AioJsp;
import aionem.net.sdk.web.jsp.utils.UtilsApi;
import lombok.NonNull;
import lombok.extern.log4j.Log4j2;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;


@WebServlet(value={"/api/jsp/*"}, name="JspServlet", description="Jsp • AIONEM.net - Jsp Servlet")

@Log4j2
public class JspServlet extends HttpServlet {

    @Override
    protected void doGet(@NonNull final HttpServletRequest request, @NonNull final HttpServletResponse response) throws ServletException, IOException {

        final String action = UtilsApi.getAction(request);

        final AioJsp aioJsp = new AioJsp(request, response);

        final PrintWriter printWriter = aioJsp.getWriter();

        final ArrayList<String> listFilePagePaths = aioJsp.invalidateCache();
        for(int i = 0; i < listFilePagePaths.size(); i++) {
            final String pagePath = listFilePagePaths.get(i);
            printWriter.println("<br>"+ (i+1) +". "+ pagePath);
        }

    }

}
