package aionem.net.sdk.web.filters;

import aionem.net.sdk.core.utils.UtilsText;
import aionem.net.sdk.web.AioWeb;
import aionem.net.sdk.web.modals.PageManager;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;


public class UrlRewriteFilter extends org.tuckey.web.filters.urlrewrite.UrlRewriteFilter {


    @Override
    public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain chain) throws IOException, ServletException {
        final AioWeb aioWeb = new AioWeb(request, response);

        final String requestUrl = aioWeb.getRequestUrl();

        boolean isSystemPath = false;
        for(final String systemPath: PageManager.SYSTEM_PATH) {
            isSystemPath = requestUrl.startsWith(systemPath);
            if(isSystemPath) break;
        }

        if(!aioWeb.isHostMatch() && !aioWeb.isLocal()) {
            final String urlQuery = aioWeb.getRequestUrlQuery();
            aioWeb.getRedirect(aioWeb.getConfEnv().getUrl(urlQuery));
        }else if(!isSystemPath) {

            if(requestUrl.lastIndexOf(".") < 0) {

                final String requestRoot = aioWeb.getRequestRoot();

                String home = "/en";
                String homes = "/en,/it,/rw,/auth,/dashboard";
                boolean isHome = UtilsText.isEmpty(requestRoot) || !homes.contains(requestRoot);

                try {

                    response.setCharacterEncoding("UTF-8");
                    response.setContentType("text/html; charset=UTF-8");

                    aioWeb.include(aioWeb.getContextPath("/ui.page" +(isHome ? home : "")+ requestUrl + "/index.jsp" +"?"+ aioWeb.getRequestQuery()));

                }catch (Exception e) {
                    aioWeb.getResponse().sendError(HttpServletResponse.SC_NOT_FOUND);
                }

            }else {

                String filePath = aioWeb.getRealPathRoot("/ui.frontend" + requestUrl);

                System.out.println(filePath);

                Path file = Path.of(filePath);

                if(Files.exists(file)) {

                    String fileName = file.getFileName().toString();

                    aioWeb.getResponse().setHeader("Content-Disposition", "inline; filename=\"" + fileName + "\"");

                    response.setContentType("text/plain");
                    response.setContentLength((int) Files.size(file));

                    try( InputStream inputStream = Files.newInputStream(file);
                         OutputStream outputStream = response.getOutputStream()) {

                        byte[] buffer = new byte[4096];
                        int bytesRead;
                        while ((bytesRead = inputStream.read(buffer)) != -1) {
                            outputStream.write(buffer, 0, bytesRead);
                        }
                    }

                } else {
                    aioWeb.getResponse().sendError(HttpServletResponse.SC_NOT_FOUND);
                }

            }

        }else {
            super.doFilter(request, response, chain);
        }

    }

}
