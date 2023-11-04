package aionem.net.sdk.jsp;

import org.tuckey.web.filters.urlrewrite.UrlRewriteFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.IOException;


public class AlnJspUrlRewriteFilter extends UrlRewriteFilter {

    @Override
    public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain chain) throws IOException, ServletException {
        final AlnJsp alnJsp = new AlnJsp(request, response);

        if(!alnJsp.isHostMatch() && !alnJsp.isLocal()) {
            final String urlQuery = alnJsp.getRequestUrlQuery();
            System.out.println("env: "+ alnJsp.getConfigEnv());
            System.out.println("host: "+ alnJsp.getConfigHost());
            System.out.println("host: "+ alnJsp.getRemoteHost());
            System.out.println("urlQuery: "+ urlQuery);
            System.out.println("configUrl1: "+ alnJsp.getConfigUrl());
            System.out.println("configUrl2: "+ alnJsp.getConfigUrl(urlQuery));
            alnJsp.getRedirect(alnJsp.getConfigUrl(urlQuery));
        }else {
            super.doFilter(request, response, chain);
        }

    }

}
