package aionem.net.sdk.web.jsp.filters;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.IOException;


public class JspExpiresFilter extends ExpiresFilter {

    @Override
    public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain chain) throws IOException, ServletException {
        super.doFilter(request, response, chain);
    }

}
