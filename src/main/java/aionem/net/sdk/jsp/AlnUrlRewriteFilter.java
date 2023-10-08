package aionem.net.sdk.jsp;

import org.tuckey.web.filters.urlrewrite.UrlRewriteFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


public class AlnUrlRewriteFilter extends UrlRewriteFilter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        final String remoteHost = request.getRemoteHost();
        if(!"66.29.143.32".equals(remoteHost) && !"0:0:0:0:0:0:0:1".equals(remoteHost)) {
            final AlnJsp alnJsp = new AlnJsp((HttpServletRequest) request, (HttpServletResponse) response);
            final String urlQuery = alnJsp.getRequestUrlQuery();
            alnJsp.sendRedirect("https://aionem.net"+urlQuery);
        }else {
            super.doFilter(request, response, chain);
        }
    }

}
