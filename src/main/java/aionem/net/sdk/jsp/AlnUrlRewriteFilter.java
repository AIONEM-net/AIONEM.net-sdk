package aionem.net.sdk.jsp;

import lombok.extern.log4j.Log4j2;
import org.tuckey.web.filters.urlrewrite.UrlRewriteFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.IOException;


public class AlnUrlRewriteFilter extends UrlRewriteFilter {

    @Override
    public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain chain) throws IOException, ServletException {
        final AlnJsp alnJsp = new AlnJsp(request, response);
        if(!"66.29.143.32".equalsIgnoreCase(alnJsp.getRemoteHost()) && !alnJsp.isLocal()) {
            final String urlQuery = alnJsp.getRequestUrlQuery();
            alnJsp.sendRedirect("https://aionem.net" + urlQuery);
        }else {
            super.doFilter(request, response, chain);
        }
    }

}
