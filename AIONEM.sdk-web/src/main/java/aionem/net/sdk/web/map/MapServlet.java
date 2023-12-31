package aionem.net.sdk.web.map;

import aionem.net.sdk.core.utils.UtilsText;
import aionem.net.sdk.web.modals.ApiResponse;
import lombok.extern.log4j.Log4j2;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;


@Log4j2
@WebServlet(urlPatterns = "/api/*")
public class MapServlet extends HttpServlet {

    private String urlPattern = "";

    @Override
    public void init() {

        if(getClass().isAnnotationPresent(RequestMapping.class)) {

            final RequestMapping requestMapping = getClass().getAnnotation(RequestMapping.class);

            urlPattern = requestMapping.value();

        }else if(getClass().isAnnotationPresent(WebServlet.class)) {

            final WebServlet webServlet = getClass().getAnnotation(WebServlet.class);

            final String[] patterns = webServlet.urlPatterns();

            if(patterns.length > 0) {
                urlPattern = patterns[0];
            }else {
                urlPattern = webServlet.value()[0];
            }

        }

        urlPattern = UtilsText.notNull(urlPattern);

        if(urlPattern.endsWith("*")) {
            urlPattern = urlPattern.substring(0, urlPattern.length() - 1);
        }
        if(urlPattern.endsWith("/")) {
            urlPattern = urlPattern.substring(0, urlPattern.length() - 1);
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

        Method methodRequest = null;
        for(final Method method : methods) {
            if(method.isAnnotationPresent(GetMapping.class) && request.getMethod().equals("GET")) {
                if(isHandleRequest(method, requestURI)) {
                    methodRequest = method;
                }
            }else if(method.isAnnotationPresent(PostMapping.class) && request.getMethod().equals("POST")) {
                if(isHandleRequest(method, requestURI)) {
                    methodRequest = method;
                }
            }else if(method.isAnnotationPresent(PutMapping.class) && request.getMethod().equals("PUT")) {
                if(isHandleRequest(method, requestURI)) {
                    methodRequest = method;
                }
            }else if(method.isAnnotationPresent(DeleteMapping.class) && request.getMethod().equals("DELETE")) {
                if(isHandleRequest(method, requestURI)) {
                    methodRequest = method;
                }
            }else if(method.isAnnotationPresent(HeadMapping.class) && request.getMethod().equals("HEAD")) {
                if(isHandleRequest(method, requestURI)) {
                    methodRequest = method;
                }
            }else if(method.isAnnotationPresent(OptionsMapping.class) && request.getMethod().equals("OPTIONS")) {
                if(isHandleRequest(method, requestURI)) {
                    methodRequest = method;
                }
            }else if(method.isAnnotationPresent(TraceMapping.class) && request.getMethod().equals("TRACE")) {
                if(isHandleRequest(method, requestURI)) {
                    methodRequest = method;
                }
            }else {

                if(methodRequest == null) {
                    if(method.getName().equals("doGet") && request.getMethod().equals("GET")) {
                        isHandleRequest(method, requestURI);
                    }else if(method.getName().equals("doPost") && request.getMethod().equals("POST")) {
                        isHandleRequest(method, requestURI);
                    }else if(method.getName().equals("doPut") && request.getMethod().equals("PUT")) {
                        isHandleRequest(method, requestURI);
                    }else if(method.getName().equals("doDelete") && request.getMethod().equals("DELETE")) {
                        isHandleRequest(method, requestURI);
                    }else if(method.getName().equals("doHead") && request.getMethod().equals("HEAD")) {
                        isHandleRequest(method, requestURI);
                    }else if(method.getName().equals("doOptions") && request.getMethod().equals("OPTIONS")) {
                        isHandleRequest(method, requestURI);
                    }else if(method.getName().equals("doTrace") && request.getMethod().equals("TRACE")) {
                        isHandleRequest(method, requestURI);
                    }
                }

            }
        }

        if(methodRequest != null) {
            try {
                methodRequest.setAccessible(true);
                methodRequest.invoke(this, request, response);
            }catch(Exception e) {
                throw new ServletException("Error invoking method", e);
            }
        }else {
            final ApiResponse apiResponse = ApiResponse.withError(HttpURLConnection.HTTP_NOT_FOUND, "Api not found");
            apiResponse.setResponse(response);
        }

    }

    private boolean isHandleRequest(final Method method, final String requestURI) {

        boolean isInvokeMethod = true;

        if(!UtilsText.isEmpty(urlPattern)) {

            String mappingValue = getMappingValue(method);

            if(!UtilsText.isEmpty(mappingValue)) {

                if(!mappingValue.startsWith("/")) {
                    mappingValue = "/" + mappingValue;
                }

                if(!requestURI.equals(urlPattern + mappingValue)) {
                    isInvokeMethod = false;
                }
            }
        }

        return isInvokeMethod;
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
