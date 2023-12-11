package aionem.net.sdk.web.jsp.map;

import aionem.net.sdk.web.jsp.AioJsp;
import org.tuckey.web.filters.urlrewrite.UrlRewriteFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.IOException;


public class JspUrlRewriteFilter extends UrlRewriteFilter {


    @Override
    public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain chain) throws IOException, ServletException {
        final AioJsp aioJsp = new AioJsp(request, response);

        if(!aioJsp.isHostMatch() && !aioJsp.isLocal()) {
            final String urlQuery = aioJsp.getRequestUrlQuery();
            aioJsp.getRedirect(aioJsp.getConfigUrl(urlQuery));
        }else {
            super.doFilter(request, response, chain);
        }

    }

}
