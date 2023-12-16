package aionem.net.sdk.web.jsp.filters;

import aionem.net.sdk.core.utils.UtilsText;
import aionem.net.sdk.web.jsp.AioJsp;
import org.tuckey.web.filters.urlrewrite.UrlRewriteFilter;

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


public class JspUrlRewriteFilter extends UrlRewriteFilter {


    @Override
    public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain chain) throws IOException, ServletException {
        final AioJsp aioJsp = new AioJsp(request, response);

        final String requestUrl = aioJsp.getRequestUrl();

        boolean isSystemPath = false;
        for(final String systemPath: AioJsp.SYSTEM_PATH) {
            isSystemPath = requestUrl.startsWith(systemPath);
            if(isSystemPath) break;
        }

        if(!aioJsp.isHostMatch() && !aioJsp.isLocal()) {
            final String urlQuery = aioJsp.getRequestUrlQuery();
            aioJsp.getRedirect(aioJsp.getConfigUrl(urlQuery));
        }else if(!isSystemPath) {

            if(requestUrl.lastIndexOf(".") < 0) {

                final String requestRoot = aioJsp.getRequestRoot();

                String home = "/en";
                String homes = "/en,/it,/rw,/auth,/dashboard";
                boolean isHome = UtilsText.isEmpty(requestRoot) || !homes.contains(requestRoot);

                try {
                    aioJsp.include(aioJsp.getContextPath("/ui.page" +(isHome ? home : "")+ requestUrl + "/index.jsp" +"?"+ aioJsp.getRequestQuery()));
                }catch (Exception e) {
                    aioJsp.getResponse().sendError(HttpServletResponse.SC_NOT_FOUND);
                }

            }else {

                String filePath = aioJsp.getRealPathRoot("/ui.frontend" + requestUrl);

                System.out.println(filePath);

                Path file = Path.of(filePath);

                if(Files.exists(file)) {

                    String fileName = file.getFileName().toString();

                    aioJsp.getResponse().setHeader("Content-Disposition", "inline; filename=\"" + fileName + "\"");

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
                    aioJsp.getResponse().sendError(HttpServletResponse.SC_NOT_FOUND);
                }

            }

        }else {
            super.doFilter(request, response, chain);
        }

    }

}
