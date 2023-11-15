package aionem.net.sdk.test;

import aionem.net.sdk.web.jsp.map.*;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


@WebServlet(urlPatterns = "/api/ok/*")
public class MapServletTest2 extends MapServlet {

    @GetMapping("/")
    public void get(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.getWriter().write("doGET request");
    }

    @GetMapping("/hello")
    public void handleGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.getWriter().write("Ok GET request");
    }

    @PostMapping("/hello")
    public void handlePost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.getWriter().write("Ok POST request");
    }

    @PutMapping("/hello")
    public void handlePut(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.getWriter().write("Ok PUT request");
    }

    @DeleteMapping("/hello")
    public void handleDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.getWriter().write("Ok DELETE request");
    }

    @OptionsMapping("/hello")
    public void handleOptions(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.getWriter().write("Ok OPTIONS request");
    }

    @HeadMapping("/hello")
    public void handleHead(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.getWriter().write("Ok HEAD request");
    }

    @TraceMapping("/hello")
    public void handleTrace(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.getWriter().write("Ok TRACE request");
    }

}
