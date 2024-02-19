package aionem.net.sdk.web.servlets;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collections;


@WebServlet(urlPatterns = "/api/proxy/*")
public class ProxyServlet extends HttpServlet {

    private static final String targetUrl = "https://95a9-41-186-61-59.ngrok-free.app";

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String pathInfo = request.getPathInfo() == null ? "" : request.getPathInfo();
        URL url = new URL(targetUrl + pathInfo + (request.getQueryString() != null ? "?" + request.getQueryString() : ""));
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        try {

            connection.setRequestMethod(request.getMethod());

            for (String headerName : Collections.list(request.getHeaderNames())) {
                connection.setRequestProperty(headerName, request.getHeader(headerName));
            }

            connection.setRequestProperty("ngrok-skip-browser-warning", "true");

            if ("POST".equalsIgnoreCase(request.getMethod()) || "PUT".equalsIgnoreCase(request.getMethod()) || "PATCH".equalsIgnoreCase(request.getMethod())) {
                connection.setDoOutput(true);
                try (OutputStream os = connection.getOutputStream(); InputStream is = request.getInputStream()) {
                    byte[] buffer = new byte[1024];
                    int bytesRead;
                    while ((bytesRead = is.read(buffer)) != -1) {
                        os.write(buffer, 0, bytesRead);
                    }
                }
            }

            response.setStatus(connection.getResponseCode());
            for (String headerKey : connection.getHeaderFields().keySet()) {
                if (headerKey != null) {
                    response.setHeader(headerKey, connection.getHeaderField(headerKey));
                }
            }

            try (InputStream is = connection.getInputStream(); OutputStream os = response.getOutputStream()) {
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = is.read(buffer)) != -1) {
                    os.write(buffer, 0, bytesRead);
                }
            }

        } finally {
            connection.disconnect();
        }

    }

}
