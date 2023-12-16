package aionem.net.sdk.web.jsp.filters;

import java.util.Enumeration;
import javax.servlet.Filter;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;

import lombok.extern.log4j.Log4j2;

@Log4j2
public abstract class FilterBase implements Filter {
    protected static final StringManager sm = StringManager.getManager("org.apache.catalina.filters");

    public FilterBase() {
    }

    public void init(FilterConfig filterConfig) throws ServletException {
        Enumeration<String> paramNames = filterConfig.getInitParameterNames();

        while(paramNames.hasMoreElements()) {
            String paramName = (String)paramNames.nextElement();
            if (!IntrospectionUtils.setProperty(this, paramName, filterConfig.getInitParameter(paramName))) {
                String msg = sm.getString("filterbase.noSuchProperty", new Object[]{paramName, this.getClass().getName()});
                if (this.isConfigProblemFatal()) {
                    throw new ServletException(msg);
                }

                log.warn(msg);
            }
        }

    }

    public void destroy() {
    }

    protected boolean isConfigProblemFatal() {
        return false;
    }
}
