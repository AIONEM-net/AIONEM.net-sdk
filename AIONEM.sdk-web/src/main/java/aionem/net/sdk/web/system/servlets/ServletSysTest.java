package aionem.net.sdk.web.system.servlets;

import aionem.net.sdk.data.beans.DataAuth;
import aionem.net.sdk.data.config.ConfApp;
import aionem.net.sdk.data.dao.I18n;
import aionem.net.sdk.web.beans.Page;
import aionem.net.sdk.web.beans.Resource;
import aionem.net.sdk.web.config.ConfEnv;
import aionem.net.sdk.web.config.Conf;
import aionem.net.sdk.web.dao.*;
import aionem.net.sdk.web.servlets.GetMapping;
import aionem.net.sdk.web.servlets.HttpServletApi;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;


@WebServlet(value={"/api/ui.system/test/*"}, name="ServletSysTest", description="Ui.System • AIONEM.net - Test Servlet")
public class ServletSysTest extends HttpServletApi {


    @GetMapping("/{id}")
    protected void doGetTest(final HttpServletRequest request, final HttpServletResponse response) throws IOException {

        response.setContentType("text/html;charset=UTF-8");
        final PrintWriter out = response.getWriter();

        out.println("<html>");
        out.println("<head>");
        out.println("<title>Ui.System • AIONEM.net</title>");
        out.println("</head>");
        out.println("<body>");

        // I18n variables
        final I18n i18n = new I18n("rw");
        final I18n i18nFr = new I18n("fr");
        out.println("<div>" + "i18n -> rw: " + i18n.get("something went wrong") + "</div>");
        out.println("<div>" + "i18n -> rw: " + i18n.get("something_went_wrong") + "</div>");
        out.println("<div>" + "i18n -> fr: " + i18nFr.get("Something went wrong") + "</div>");
        out.println("<div>" + "i18n -> fr: " + i18nFr.get("something_went_wrong") + "</div>");
        out.println("<div>---------------------------------------------</div>");


        // ConfigApp
        final ConfApp confApp = ConfApp.getInstance();
        out.println("<div>" + "confApp key -> " + ConfApp.isUsePoolDataSource() + "</div>");
        out.println("<div>" + "confApp key -> " + ConfApp.getDBDriver() + "</div>");
        out.println("<div>" + "confApp key -> " + ConfApp.getDBConnection() + "</div>");
        out.println("<div>" + "confApp key -> " + ConfApp.getDBUrl() + "</div>");
        out.println("<div>" + "confApp key -> " + ConfApp.getDBConnectionUrl() + "</div>");
        out.println("<div>" + "confApp key -> " + ConfApp.getDBHost() + "</div>");
        out.println("<div>" + "confApp key -> " + ConfApp.getDBPort() + "</div>");
        out.println("<div>" + "confApp key -> " + ConfApp.getDBName() + "</div>");
        out.println("<div>" + "confApp key -> " + ConfApp.getDBUser() + "</div>");
        out.println("<div>" + "confApp key -> " + ConfApp.getDBPassword() + "</div>");
        out.println("<div>" + "confApp data -> " + confApp + "</div>");
        out.println("<div>---------------------------------------------</div>");


        // Config
        Conf conf = new Conf();
        out.println("<div>" + "config -> " + conf.getName() + "</div>");
        out.println("<div>" + "config env -> " + conf.getEnv() + "</div>");
        out.println("<div>" + "config key -> " + conf.get("host") + "</div>");
        out.println("<div>" + "config data -> " + conf + "</div>");
        out.println("<div>---------------------------------------------</div>");


        // ConfEnv
        ConfEnv confEnv = ConfEnv.getInstance();
        out.println("<div>" + "confEnv -> " + confEnv.getName() + "</div>");
        out.println("<div>" + "confEnv env -> " + confEnv.getEnv() + "</div>");
        out.println("<div>" + "confEnv key -> " + confEnv.get("home") + "</div>");
        out.println("<div>" + "confEnv key -> " + confEnv.get("sites") + "</div>");
        out.println("<div>" + "confEnv key -> " + confEnv.getUrl() + "</div>");
        out.println("<div>" + "confEnv data -> " + confEnv + "</div>");
        out.println("<div>---------------------------------------------</div>");


        // DataSystem
        DataAuth dataAuth = new DataAuth();
        dataAuth.init(dataAuth);
        dataAuth.put("id", 4);
        out.println("<div>" + "data var -> " + dataAuth.getId() + "</div>");
        out.println("<div>" + "data key -> " + dataAuth.get("id") + "</div>");
        out.println("<div>" + "data -> " + dataAuth + "</div>");
        out.println("<div>---------------------------------------------</div>");


        // Page
        Page page1 = new Page("/en/hosting/domains");
        out.println("<div>" + "page -> " + page1.getTitle() + "</div>");
        out.println("<div>" + "page -> " + page1.getPath() + "</div>");
        out.println("<div>" + "page -> " + page1.getUrl() + "</div>");
        out.println("<div>" + "page -> " + page1.listChildren().size() + "</div>");
        out.println("<div>" + "page -> " + page1.getProperties() + "</div>");
        out.println("<div>" + "page -> " + page1.getTemplate() + "</div>");
        out.println("<div>" + "page -> " + page1.toResource() + "</div>");
        out.println("<div>" + "page -> " + page1.getParent() + "</div>");

        Page page2 = new Page("/en");
        out.println("<div>" + "page -> " + page2.getTitle() + "</div>");
        out.println("<div>" + "page -> " + page2.getPath() + "</div>");
        out.println("<div>" + "page -> " + page2.getUrl() + "</div>");
        out.println("<div>" + "page -> " + page2.listChildren().size() + "</div>");
        out.println("<div>" + "page -> " + page2.getProperties() + "</div>");
        out.println("<div>" + "page -> " + page2.getTemplate() + "</div>");
        out.println("<div>" + "page -> " + page2.toResource() + "</div>");
        out.println("<div>" + "page -> " + page2.getParent() + "</div>");
        out.println("<div>---------------------------------------------</div>");


        // PageManager
        PageManager pageManager = new PageManager();
        out.println("<div>" + "pageManager -> " + ResourceResolver.getRealPathPage() + "</div>");
        out.println("<div>" + "pageManager copy -> " + pageManager.copy(page1, "/it/copied") + "</div>");
        out.println("<div>" + "pageManager move -> " + pageManager.move(page1, "/it/moved") + "</div>");
        for(Page resource : pageManager.getListPagesRoot()) {
            out.println("<div>" + "Resource file -> " + resource.getPath() + "</div>");
        }
        out.println("<div>---------------------------------------------</div>");


        // I18nManager
        final I18nManager i18nManager = new I18nManager();
        for(Resource folderI18n : i18nManager.getListFolders()) {
            out.println("<div>" + "I18n folder -> " + folderI18n.getRelativePath() + "</div>");
        }
        for(Resource fileI18n : i18nManager.getListI18n()) {
            out.println("<div>" + "I18n file -> " + fileI18n.getRelativePath() + "</div>");
        }
        out.println("<div>---------------------------------------------</div>");


        // ConfigManager
        final ConfManager confManager = new ConfManager();
        for(Resource folderConfig : confManager.getListFolders()) {
            out.println("<div>" + "Config folder -> " + folderConfig.getRelativePath() + "</div>");
        }
        for(Resource fileConfig : confManager.getListConfs()) {
            out.println("<div>" + "Config file -> " + fileConfig.getRelativePath() + "</div>");
        }
        out.println("<div>---------------------------------------------</div>");


        // DriveManager
        final DriveManager driveManager = new DriveManager();
        final Resource drive = driveManager.getFile("/uploads/logo.png");
        out.println("<div>" + "Drive -> " + drive + "</div>");
        out.println("<div>" + "driveManager move -> " + driveManager.move(drive, "/uploads/copied") + "</div>");
        out.println("<div>" + "driveManager copy -> " + driveManager.copy(drive, "/uploads/copied") + "</div>");
        for(Resource file : driveManager.getRoots()) {
            out.println("<div>" + "Drive files -> " + file.getRelativePath() + "</div>");
        }
        for(Resource file : driveManager.getFiles("uploads")) {
            out.println("<div>" + "Drive uploads -> " + file.getRelativePath() + "</div>");
        }
        for(Resource file : driveManager.getFiles("uploads/images")) {
            out.println("<div>" + "Drive images -> " + file.getRelativePath() + "</div>");
        }
        out.println("<div>---------------------------------------------</div>");


        // AppsManager
        final AppsManager appsManager = new AppsManager();
        for(Resource cmp : appsManager.listApps()) {
            out.println("<div>" + "Apps root -> " + cmp.getSystemPath() + "</div>");
        }
        for(Resource file : appsManager.listComponents()) {
            out.println("<div>" + "Apps cmp -> " + file.getSystemPath() + "</div>");
        }
        out.println("<div>---------------------------------------------</div>");


        // PageManager
        out.println("<div>"+ "saveSiteMap -> "+ pageManager.saveSiteMap() +"</div>");
        out.println("<div>---------------------------------------------</div>");


        // DaoWebAuth
        final DaoWebAuth daoAuth = DaoWebAuth.getInstance();
        out.println("<div>"+ "Auth user -> "+ daoAuth.getUser("admin@aionem.net") +"</div>");
        out.println("<div>"+ "Auth user -> "+ daoAuth.getUser("editor@aionem.net") +"</div>");
        out.println("<div>"+ "Auth exist admin -> "+ daoAuth.getUser("admin@aionem.net") +"</div>");
        out.println("<div>"+ "Auth exist test -> "+ daoAuth.getUser("test@aionem.net") +"</div>");
        out.println("<div>"+ "Auth exist isAuthenticated -> "+ daoAuth.isAuthenticated("admin@aionem.net", "123") +"</div>");
        out.println("<div>"+ "Auth exist isAuthenticated -> "+ daoAuth.isAuthenticated("admin@aionem.net", "test") +"</div>");
        out.println("<div>"+ "Auth exist isAuthenticated -> "+ daoAuth.isAuthenticated("test@aionem.net", "123") +"</div>");
        out.println("<div>---------------------------------------------</div>");


        out.println("</body>");
        out.println("</html>");
        out.close();
    }

}
