package aionem.net.sdk.web.system.servlets;

import aionem.net.sdk.data.beans.DataAuth;
import aionem.net.sdk.data.config.ConfApp;
import aionem.net.sdk.data.dao.I18n;
import aionem.net.sdk.web.beans.Page;
import aionem.net.sdk.web.config.ConfEnv;
import aionem.net.sdk.web.config.Config;
import aionem.net.sdk.web.dao.*;
import aionem.net.sdk.web.servlets.HttpServletApi;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;


@WebServlet(value={"/api/ui.system/test/*"}, name="ServletSysTest", description="Ui.System • AIONEM.net - Test Servlet")
public class ServletSysTest extends HttpServletApi {


    @Override
    protected void doGet(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {

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

        final ConfApp confApp = ConfApp.getInstance();
        out.println("<div>" + "confApp env -> " + confApp.getEnv() + "</div>");
        out.println("<div>" + "confApp key -> " + confApp.get("home") + "</div>");
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
        out.println("<div>" + "confApp data env -> " + confApp + "</div>");
        out.println("<div>" + "confApp data base -> " + confApp.getBaseData() + "</div>");
        out.println("<div>---------------------------------------------</div>");


        // Output for Config
        Config conf = new Config();
        out.println("<div>" + "config -> " + conf.getName() + "</div>");
        out.println("<div>" + "config env -> " + conf.getEnv() + "</div>");
        out.println("<div>" + "config key -> " + conf.get("host") + "</div>");
        out.println("<div>" + "config data env -> " + conf + "</div>");
        out.println("<div>" + "config data base -> " + conf.getBaseData() + "</div>");
        out.println("<div>---------------------------------------------</div>");


        // Output for ConfEnv
        ConfEnv confEnv = ConfEnv.getInstance();
        out.println("<div>" + "confEnv -> " + confEnv.getName() + "</div>");
        out.println("<div>" + "confEnv key -> " + confEnv.get("sites") + "</div>");
        out.println("<div>" + "confEnv env -> " + confEnv.getEnv() + "</div>");
        out.println("<div>" + "confEnv key -> " + confEnv.getUrl() + "</div>");
        out.println("<div>" + "confEnv data env -> " + confEnv + "</div>");
        out.println("<div>" + "confEnv data base -> " + confEnv.getBaseData() + "</div>");
        out.println("<div>---------------------------------------------</div>");


        // Output for DataSystem
        DataAuth dataAuth = new DataAuth();
        dataAuth.put("id", 4);
        out.println("<div>" + "data var -> " + dataAuth.getId() + "</div>");
        out.println("<div>" + "data key -> " + dataAuth.get("id") + "</div>");
        out.println("<div>" + "data -> " + dataAuth + "</div>");
        out.println("<div>---------------------------------------------</div>");


        // Output for Page
        Page page1 = new Page("/en/hosting/domains");
        out.println("<div>" + "page -> " + page1.getTitle() + "</div>");
        out.println("<div>" + "page -> " + page1.getPath() + "</div>");
        out.println("<div>" + "page -> " + page1.getUrl() + "</div>");
        out.println("<div>" + "page -> " + page1.listChildren().size() + "</div>");
        out.println("<div>" + "page -> " + page1.getProperties() + "</div>");
        out.println("<div>" + "page -> " + page1.getResourceType() + "</div>");
        out.println("<div>" + "page -> " + page1.getTemplate() + "</div>");
        out.println("<div>" + "page -> " + page1.getTemplatePath() + "</div>");
        out.println("<div>" + "page -> " + page1.getMenu() + "</div>");
        out.println("<div>" + "page -> " + page1.getContent() + "</div>");
        out.println("<div>" + "page -> " + page1.getResource() + "</div>");
        out.println("<div>" + "page -> " + page1.getParent() + "</div>");

        Page page2 = new Page("/en");
        out.println("<div>" + "page -> " + page2.getTitle() + "</div>");
        out.println("<div>" + "page -> " + page2.getPath() + "</div>");
        out.println("<div>" + "page -> " + page2.getUrl() + "</div>");
        out.println("<div>" + "page -> " + page2.listChildren().size() + "</div>");
        out.println("<div>" + "page -> " + page2.getProperties() + "</div>");
        out.println("<div>" + "page -> " + page2.getResourceType() + "</div>");
        out.println("<div>" + "page -> " + page2.getTemplate() + "</div>");
        out.println("<div>" + "page -> " + page2.getTemplatePath() + "</div>");
        out.println("<div>" + "page -> " + page2.getMenu() + "</div>");
        out.println("<div>" + "page -> " + page2.getContent() + "</div>");
        out.println("<div>" + "page -> " + page2.getResource() + "</div>");
        out.println("<div>" + "page -> " + page2.getParent() + "</div>");
        out.println("<div>---------------------------------------------</div>");


        // Output for PageManager
        PageManager pageManager = new PageManager();
        out.println("<div>" + "pageManager -> " + ResourceResolver.getRealPathPage() + "</div>");
        out.println("<div>---------------------------------------------</div>");


        // Output for I18nManager
        final I18nManager i18nManager = new I18nManager();
        for (File folderI18n : i18nManager.getListFoldersI18n()) {
            out.println("<div>" + "I18n folder -> " + folderI18n.getPath() + "</div>");
        }
        for (File fileI18n : i18nManager.getListI18nFiles()) {
            out.println("<div>" + "I18n file -> " + fileI18n.getPath() + "</div>");
        }
        out.println("<div>---------------------------------------------</div>");


        // Output for ConfigManager
        final ConfigManager configManager = new ConfigManager();
        for (File folderConfig : configManager.getListFoldersConfig()) {
            out.println("<div>" + "Config folder -> " + folderConfig.getPath() + "</div>");
        }
        for (File fileConfig : configManager.getListFilesConfig()) {
            out.println("<div>" + "Config file -> " + fileConfig.getPath() + "</div>");
        }
        out.println("<div>---------------------------------------------</div>");


        // Output for DriveManager
        final DriveManager driveManager = new DriveManager();
        out.println("<div>" + "Drive -> " + driveManager.getFolder() + "</div>");
        for (File file : driveManager.getFiles()) {
            out.println("<div>" + "Drive files -> " + file.getPath() + "</div>");
        }
        for (File file : driveManager.getFiles("uploads")) {
            out.println("<div>" + "Drive uploads -> " + file.getPath() + "</div>");
        }
        for (File file : driveManager.getFiles("uploads/images")) {
            out.println("<div>" + "Drive images -> " + file.getPath() + "</div>");
        }

        out.println("</body>");
        out.println("</html>");
        out.close();
    }

}
