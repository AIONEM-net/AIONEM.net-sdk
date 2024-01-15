package aionem.net.sdk.web.filters;

import aionem.net.sdk.data.utils.UtilsResource;
import aionem.net.sdk.web.AioWeb;
import aionem.net.sdk.web.beans.Page;
import aionem.net.sdk.web.beans.Resource;
import aionem.net.sdk.web.config.ConfEnv;
import aionem.net.sdk.web.dao.ResourceResolver;
import org.tuckey.web.filters.urlrewrite.UrlRewriteFilter;

import javax.servlet.*;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class FilterUrlRewrite extends UrlRewriteFilter {


    @Override
    public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain chain) throws IOException, ServletException {

        final AioWeb aioWeb = new AioWeb(request, response);

        final String requestPath = aioWeb.getRequestURI();

        final boolean isUiPage = !requestPath.contains(".");
        final boolean isSystemPath = ResourceResolver.isSystemPath(requestPath);

        if(!aioWeb.isHostMatch() && !aioWeb.isRemoteLocal()) {

            final String urlQuery = aioWeb.getRequestUrlQuery();
            aioWeb.sendRedirect(aioWeb.getConfEnv().getUrl(urlQuery));

        }else if(isUiPage && !isSystemPath) {

            try {

                aioWeb.getResponse().setCharacterEncoding("UTF-8");
                aioWeb.getResponse().setContentType("text/html; charset=UTF-8");

                if(aioWeb.isRoot()) {
                    responsePage(aioWeb, aioWeb.getCurrentPage(), HttpServletResponse.SC_OK);
                }else if(aioWeb.isHome()) {
                    final String url = "/"+ aioWeb.getRequestQuery(true);
                    aioWeb.sendRedirect(url);
                }else if(aioWeb.isUnderHome()) {
                    final String url = aioWeb.getRequestUrlQuery().substring(ConfEnv.getInstance().getHome().length());
                    aioWeb.sendRedirect(url);
                }else {
                    responsePage(aioWeb, aioWeb.getCurrentPage(), HttpServletResponse.SC_OK);
                }

            }catch(final Exception e) {
                final Page errorPage;
                if(!aioWeb.getCurrentPage().exists()) {
                    errorPage = new Page(ConfEnv.getInstance().getError(404));
                    responsePage(aioWeb, errorPage, HttpServletResponse.SC_NOT_FOUND);
                }else {
                    errorPage = new Page(ConfEnv.getInstance().getError(500));
                    responsePage(aioWeb, errorPage, HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                }
            }

        }else {

            if(requestPath.startsWith("/ui.page")) {

                if(requestPath.endsWith("/.png")) {

                    final String filePath = UtilsResource.getRealPathRoot(requestPath);
                    final Resource resource = new Resource(filePath);
                    responseFile(aioWeb, resource);

                }else {
                    final String url = requestPath.substring("/ui.page".length());
                    aioWeb.sendRedirect(url);
                }

                return;
            }else if(requestPath.startsWith("/ui.drive")) {

                final String filePath = UtilsResource.getRealPathRoot(requestPath);
                final Resource resource = new Resource(filePath);
                responseFile(aioWeb, resource);

                return;
            }else if(requestPath.startsWith("/ui.frontend")) {
                chain.doFilter(request, response);
                return;
            }else if(requestPath.startsWith("/ui.template")) {

                if(requestPath.endsWith("/.png")) {

                    final String filePath = ResourceResolver.getRealPathWebInf(requestPath);
                    final Resource resource = new Resource(filePath);
                    responseFile(aioWeb, resource);

                }else {
                    chain.doFilter(request, response);
                }

                return;
            }else if(requestPath.startsWith("/ui.system")) {
                chain.doFilter(request, response);
                return;
            }else if(requestPath.startsWith("/api")) {
                chain.doFilter(request, response);
                return;
            }

            Pattern pattern = Pattern.compile("^/drive/(.+)$");
            Matcher matcher = pattern.matcher(requestPath);
            if(matcher.matches()) {

                final String group1 = matcher.group(1);

                final String path = "/ui.drive/" + group1;
                final String filePath = UtilsResource.getRealPathRoot(path);
                final Resource resource = new Resource(filePath);

                responseFile(aioWeb, resource);
                return;
            }

            pattern = Pattern.compile("^/assets/([^/]+)/(.+)$");
            matcher = pattern.matcher(requestPath);
            if(matcher.matches()) {

                final String group1 = matcher.group(1);
                final String group2 = matcher.group(2);

                final String path = "/ui.frontend/" + group1 + "/resources/" + group2;
                final String filePath = UtilsResource.getRealPathRoot(path);
                final Resource resource = new Resource(filePath);

                responseFile(aioWeb, resource);
                return;
            }

            pattern = Pattern.compile("^/cdn/(.+)$");
            matcher = pattern.matcher(requestPath);
            if(matcher.matches()) {

                final String group1 = matcher.group(1);

                final String path = "/ui.frontend/" + group1;
                final String filePath = UtilsResource.getRealPathRoot(path);
                final Resource resource = new Resource(filePath);

                responseFile(aioWeb, resource);
                return;
            }

            final String filePath = UtilsResource.getRealPathRoot("/ui.frontend" + requestPath);
            final Resource resource = new Resource(filePath);
            responseFile(aioWeb, resource);
        }

    }

    private void responsePage(final AioWeb aioWeb, final Page currentPage, final int status) throws ServletException, IOException {

        if(currentPage.toResource().child( "index.html").exists()) {

            final String urlIndexQuery = currentPage.getPath()
                    + (!currentPage.getPath().endsWith("/") ? "/" : "")
                    + "index.html"
                    +"?"+ aioWeb.getRequestQuery();

            aioWeb.include("/ui.page" + urlIndexQuery);

        }else {
            if(status == HttpServletResponse.SC_OK) {
                aioWeb.setup();
            }else {
                aioWeb.setRequestAttribute("currentPage", currentPage);
            }
            final String templatePath = UtilsResource.path("/WEB-INF/ui.template", currentPage.getTemplate(), "/.jsp");
            aioWeb.include(templatePath);
            aioWeb.getPageManager().cache(aioWeb, currentPage.isCache());
        }

    }

    private void responseFile(final AioWeb aioWeb, final Resource resource) throws IOException {

        final Resource resourceFile;
        if(resource.exists() && resource.isFolder()) {
            resourceFile = resource.child(".file");
        }else {
            resourceFile = resource;
        }

        if(resourceFile.exists() && resourceFile.isFile()) {

            final String fileName = resource.getName();

            aioWeb.getResponse().setContentType("text/plain");
            aioWeb.getResponse().setContentLength((int) resourceFile.getSize());
            aioWeb.setHeader("Content-Disposition", "inline; filename=\"" + fileName + "\"");

            try( final InputStream inputStream = resourceFile.getInputStream();
                 final OutputStream outputStream = aioWeb.getResponse().getOutputStream()) {

                final byte[] buffer = new byte[4096];
                int bytesRead;
                while((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
            }

        }else {
            aioWeb.sendError(HttpServletResponse.SC_NOT_FOUND);
        }

    }

}
