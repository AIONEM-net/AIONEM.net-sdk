package aionem.net.sdk.web.modals;

import aionem.net.sdk.core.utils.UtilsText;
import aionem.net.sdk.data.utils.UtilsResource;
import aionem.net.sdk.web.AioWeb;
import aionem.net.sdk.web.dao.PageManager;
import aionem.net.sdk.web.dao.ResourceResolver;
import lombok.Data;
import lombok.extern.log4j.Log4j2;

import java.io.File;
import java.util.ArrayList;
import java.util.Objects;


@Log4j2
public @Data class Page {

    private String title = "";
    private String subTitle = "";
    private String navTitle = "";
    private String pageTitle = "";
    private String brandSlug = "";
    private String headline = "";
    private String description = "";
    private String path = "";
    private String url = "";
    private String redirect = "";
    private String icon = "";
    private String language = "";
    private String template = "page";
    private String resourceType = "";
    private int order = 0;
    private boolean isRoot = false;
    private boolean isSeo = true;
    private boolean pwa = true;
    private boolean isAuthAllow = true;
    private boolean authAllowAdmin = true;
    private boolean authAllowUser = true;
    private boolean authDisableAuth = false;
    private Properties properties = new Properties();

    public Page() {

    }

    public Page(final AioWeb aioWeb) {
        init(aioWeb);
    }

    public Page(final String path) {
        init(path);
    }

    public Page(final String path, final Properties properties) {
        init(path, properties);
    }

    public Page(final String title, final String path, final String icon) {
        this.title = title;
        this.subTitle = title;
        this.navTitle = title;
        this.pageTitle = title;
        this.icon = icon;
        init(path);
    }

    public void init(final AioWeb aioWeb) {
        this.path = aioWeb.getServletPage();
        this.url = aioWeb.getContextServletPage();
        init(this.path);
    }

    public void init(final String path) {
        final File filePage = new File(ResourceResolver.getRealPathPage(path));
        init(path, new Properties(new File(filePage, "properties.json")));
    }

    public void init(final String path, final Properties properties) {
        init(properties);

        if(!UtilsText.isEmpty(path) && !path.equals("/")) {
            this.path = UtilsResource.getRelativePath(path);
            this.url = ConfEnv.getInstance().getContextPath(path);
        }

        String title = this.path
                .replace("/index.jsp", "")
                .replace("/index.html", "")
                .replace(".jsp", "")
                .replace(".html", "")
                .replace("-", " ")
                .replace("_", " ")
                .replace("//", "");
        if(title.contains("/")) {
            title = title.substring(title.lastIndexOf("/") + 1);
        }
        title = UtilsText.capitalizeFirstLetter(title);
        setTitles(title);
    }

    public void init(final Page page) {
        if(isRoot || UtilsText.isEmpty(this.title)) {
            this.title = page.getTitle();
            this.navTitle = page.getNavTitle();
            this.pageTitle = page.getPageTitle();
            this.brandSlug = page.getBrandSlug();
            this.path = page.getPath();
            this.url = page.getUrl();
            this.icon = page.getIcon();
            this.properties = page.getProperties();
        }
    }

    public void init(final Properties properties) {
        if(properties != null && !properties.isEmpty()) {
            this.properties = properties;
            this.title = properties.get("title", title);
            this.navTitle = properties.get("navTitle", navTitle);
            this.pageTitle = properties.get("pageTitle", pageTitle);
            this.brandSlug = properties.get("brandSlug", brandSlug);
            this.headline = properties.get("headline", headline);
            this.description = properties.get("description", description);
            this.url = properties.get("url", url);
            this.redirect = properties.get("redirect", redirect);
            this.icon = properties.get("icon", icon);
            this.template = properties.get("template", template);
            this.resourceType = properties.get("resourceType", resourceType);
            this.isRoot = properties.get("isRoot", isRoot);
            this.order = properties.get("order", order);
        }else {
            this.properties = new Properties();
        }
    }

    private void setTitles(final String title) {
        this.title = UtilsText.notEmpty(this.title, title);
        this.subTitle = UtilsText.notEmpty(this.subTitle, title);
        this.navTitle = UtilsText.notEmpty(this.navTitle, title);
        this.pageTitle = UtilsText.notEmpty(this.pageTitle, title);
    }

    public String getName() {
        return path.substring(path.lastIndexOf("/"));
    }

    public String getNavTitle() {
        return UtilsText.notEmpty(navTitle, pageTitle, title);
    }

    public String getMenuTitle() {
        return UtilsText.notEmpty(pageTitle, navTitle, title);
    }

    public String getFullTitle(final Page homePage) {
        if(!homePage.equals(this)) {
            return getTitle() + homePage.getBrandSlug();
        }else {
            return getTitle();
        }
    }

    public String getContent() {
        return "/ui.page"+ path +"/" + "content.jsp";
    }

    public String getTemplate() {
        return UtilsText.notEmpty(template, "page");
    }

    public String getTemplatePath() {
        return "/WEB-INF/ui.template/"+ getTemplate() +"/.jsp";
    }

    public ArrayList<Page> listChildren() {
        return new PageManager().getListPages(this);
    }

    public boolean isAuthAllow(final boolean isAdmins, final boolean isUsers, final boolean isAuths) {
        if(isAuths && authDisableAuth) return false;
        if(isUsers && !authAllowUser) return false;
        if(isAdmins && !authAllowAdmin) return false;
        return true;
    }

    private boolean exists() {
        final File file = getFile();
        return file != null && file.exists();
    }

    private File getFile() {
        return UtilsResource.getResourceFileRoot("/ui.page" + path);
    }

    public File getParent() {
        final File file = getFile();
        return file != null && file.exists() ? file.getParentFile() : null;
    }

    @Override
    public String toString() {
        return properties.toString();
    }

    @Override
    public boolean equals(final Object object) {
        if(this == object) return true;
        if(object == null || getClass() != object.getClass()) return false;
        final Page that = (Page) object;
        return Objects.equals(path, that.path);
    }

    @Override
    public int hashCode() {
        return Objects.hash(path);
    }

}
