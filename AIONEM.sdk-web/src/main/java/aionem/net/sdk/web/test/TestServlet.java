package aionem.net.sdk.web.test;

import aionem.net.sdk.web.AioWeb;
import aionem.net.sdk.web.utils.UtilsApi;
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
public class TestServlet extends HttpServlet {

    @Override
    protected void doGet(@NonNull final HttpServletRequest request, @NonNull final HttpServletResponse response) throws ServletException, IOException {

        final String action = UtilsApi.getAction(request);

        final AioWeb aioWeb = new AioWeb(request, response);

        final PrintWriter printWriter = aioWeb.getWriter();

        final ArrayList<String> listFilePagePaths = aioWeb.invalidateCache();
        for(int i = 0; i < listFilePagePaths.size(); i++) {
            final String pagePath = listFilePagePaths.get(i);
            printWriter.println("<br>"+ (i+1) +". "+ pagePath);
        }

    }

}
