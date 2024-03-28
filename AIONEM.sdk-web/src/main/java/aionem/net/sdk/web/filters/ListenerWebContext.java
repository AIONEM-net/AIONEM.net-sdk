package aionem.net.sdk.web.filters;

import aionem.net.sdk.web.servlets.HttpServletApi;

import javax.servlet.*;


public class ListenerWebContext implements ServletContextListener {


    @Override
    public void contextInitialized(final ServletContextEvent servletContextEvent) {
        final ServletContext context = servletContextEvent.getServletContext();

        final ServletRegistration.Dynamic servletRegistration = context.addServlet("MapServlet", HttpServletApi.class.getName());
        servletRegistration.addMapping("/api/*");

        final FilterRegistration.Dynamic filterRegistration = context.addFilter("FilterUrlRewrite", FilterUrlRewrite.class.getName());
        filterRegistration.addMappingForUrlPatterns(null, true, "/*");
    }

    @Override
    public void contextDestroyed(final ServletContextEvent servletContextEvent) {

    }

}
