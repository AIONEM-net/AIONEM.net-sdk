package aionem.net.sdk.test;

import aionem.net.sdk.jsp.map.*;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


@WebServlet(urlPatterns = "/api/go/*")
public class MapServletTest1 extends MapServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.getWriter().write("doGET request");
    }

    @GetMapping("/hello")
    public void handleGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.getWriter().write("Go GET request");
    }

    @PostMapping("/hello")
    public void handlePost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.getWriter().write("Go POST request");
    }

    @PutMapping("/hello")
    public void handlePut(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.getWriter().write("Go PUT request");
    }

    @DeleteMapping("/hello")
    public void handleDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.getWriter().write("Go DELETE request");
    }

    @OptionsMapping("/hello")
    public void handleOptions(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.getWriter().write("Go OPTIONS request");
    }

    @HeadMapping("/hello")
    public void handleHead(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.getWriter().write("Go HEAD request");
    }

    @TraceMapping("/hello")
    public void handleTrace(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.getWriter().write("Go TRACE request");
    }

}
