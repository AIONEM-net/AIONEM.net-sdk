package aionem.net.sdk.web.jsp.map;

import aionem.net.sdk.core.utils.AlnUtilsText;
import lombok.extern.log4j.Log4j2;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Method;


@Log4j2
@WebServlet(urlPatterns = "/api/*")
public class MapServlet extends HttpServlet {

    private String urlPattern = "";

    @Override
    public void init() {

        if(getClass().isAnnotationPresent(WebServlet.class)) {

            final WebServlet webServlet = getClass().getAnnotation(WebServlet.class);

            final String[] patterns = webServlet.urlPatterns();

            if (patterns.length > 0) {
                urlPattern = patterns[0];
            }
            urlPattern = AlnUtilsText.notNull(urlPattern);

            if (urlPattern.endsWith("*")) {
                urlPattern = urlPattern.substring(0, urlPattern.length() - 1);
            }
            if (urlPattern.endsWith("/")) {
                urlPattern = urlPattern.substring(0, urlPattern.length() - 1);
            }
        }

    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        super.doGet(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        super.doPost(req, resp);
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        super.doPut(req, resp);
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        super.doDelete(req, resp);
    }

    @Override
    protected void doHead(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        super.doHead(req, resp);
    }

    @Override
    protected void doOptions(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        super.doOptions(req, resp);
    }

    @Override
    protected void doTrace(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        super.doTrace(req, resp);
    }

    @Override
    public void service(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {

        final String requestURI = request.getRequestURI();
        final Method[] methods = this.getClass().getDeclaredMethods();

        for(Method method : methods) {
            if(method.isAnnotationPresent(GetMapping.class) && request.getMethod().equals("GET")) {
                handleRequest(method, request, response, requestURI);
            }else if(method.isAnnotationPresent(PostMapping.class) && request.getMethod().equals("POST")) {
                handleRequest(method, request, response, requestURI);
            }else if(method.isAnnotationPresent(PutMapping.class) && request.getMethod().equals("PUT")) {
                handleRequest(method, request, response, requestURI);
            }else if(method.isAnnotationPresent(DeleteMapping.class) && request.getMethod().equals("DELETE")) {
                handleRequest(method, request, response, requestURI);
            }else if(method.isAnnotationPresent(HeadMapping.class) && request.getMethod().equals("HEAD")) {
                handleRequest(method, request, response, requestURI);
            }else if(method.isAnnotationPresent(OptionsMapping.class) && request.getMethod().equals("OPTIONS")) {
                handleRequest(method, request, response, requestURI);
            }else if(method.isAnnotationPresent(TraceMapping.class) && request.getMethod().equals("TRACE")) {
                handleRequest(method, request, response, requestURI);
            }else if(method.getName().equals("doGet") && request.getMethod().equals("GET")) {
                handleRequest(method, request, response, requestURI);
            }else if(method.getName().equals("doPost") && request.getMethod().equals("POST")) {
                handleRequest(method, request, response, requestURI);
            }else if(method.getName().equals("doPut") && request.getMethod().equals("PUT")) {
                handleRequest(method, request, response, requestURI);
            }else if(method.getName().equals("doDelete") && request.getMethod().equals("DELETE")) {
                handleRequest(method, request, response, requestURI);
            }else if(method.getName().equals("doHead") && request.getMethod().equals("HEAD")) {
                handleRequest(method, request, response, requestURI);
            }else if(method.getName().equals("doOptions") && request.getMethod().equals("OPTIONS")) {
                handleRequest(method, request, response, requestURI);
            }else if(method.getName().equals("doTrace") && request.getMethod().equals("TRACE")) {
                handleRequest(method, request, response, requestURI);
            }
        }

    }

    private void handleRequest(final Method method, final HttpServletRequest request, final HttpServletResponse response, final String requestURI) throws ServletException, IOException {

        boolean isInvokeMethod = true;

        if(!AlnUtilsText.isEmpty(urlPattern)) {

            String mappingValue = getMappingValue(method);

            if(!AlnUtilsText.isEmpty(mappingValue)) {

                if(!mappingValue.startsWith("/")) {
                    mappingValue = "/" + mappingValue;
                }

                if(!requestURI.equals(urlPattern + mappingValue)) {
                    isInvokeMethod = false;
                }
            }
        }

        System.out.println(isInvokeMethod);

        if(isInvokeMethod) {
            try {
                method.setAccessible(true);
                method.invoke(this, request, response);
            }catch(Exception e) {
                throw new ServletException("Error invoking method", e);
            }
        }

    }

    private String getMappingValue(final Method method) {
        if(method.isAnnotationPresent(GetMapping.class)) {
            return method.getAnnotation(GetMapping.class).value();
        }else if(method.isAnnotationPresent(PostMapping.class)) {
            return method.getAnnotation(PostMapping.class).value();
        }else if(method.isAnnotationPresent(PutMapping.class)) {
            return method.getAnnotation(PutMapping.class).value();
        }else if(method.isAnnotationPresent(DeleteMapping.class)) {
            return method.getAnnotation(DeleteMapping.class).value();
        }else if(method.isAnnotationPresent(HeadMapping.class)) {
            return method.getAnnotation(HeadMapping.class).value();
        }else if(method.isAnnotationPresent(OptionsMapping.class)) {
            return method.getAnnotation(OptionsMapping.class).value();
        }else if(method.isAnnotationPresent(TraceMapping.class)) {
            return method.getAnnotation(TraceMapping.class).value();
        }
        return "";
    }

}
