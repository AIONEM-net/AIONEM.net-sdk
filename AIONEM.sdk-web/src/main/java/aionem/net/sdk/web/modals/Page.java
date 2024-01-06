package aionem.net.sdk.web.modals;

import aionem.net.sdk.core.utils.UtilsText;
import aionem.net.sdk.data.Data;
import aionem.net.sdk.web.AioWeb;
import aionem.net.sdk.web.dao.PageManager;
import aionem.net.sdk.web.dao.ResourceResolver;
import lombok.extern.log4j.Log4j2;

import java.util.ArrayList;
import java.util.Objects;


@Log4j2
public @lombok.Data class Page {

    private String path = "";
    private String url = "";
    private String name = "";
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

    public Page(final Resource resourcePage) {
        init(resourcePage);
    }

    public void init(final AioWeb aioWeb) {
        this.path = aioWeb.getServletPage();
        this.url = aioWeb.getContextServletPage();
        init(this.path);
    }

    public void init(final String path) {
        final String realPathPage = ResourceResolver.getRealPathPage(path);
        final Resource resourcePage = new Resource(realPathPage);
        init(path, new Properties(resourcePage.child(Properties.PROPERTIES_JSON)));
    }

    public void init(final Resource resourcePage) {
        final String realPathPage = ResourceResolver.getRealPathPage(resourcePage.getPath());
        init(realPathPage, new Properties(resourcePage.child(Properties.PROPERTIES_JSON)));
    }

    public void init(final String path, final Properties properties) {
        init(properties);

        if(UtilsText.isEmpty(path) || path.equals("/")) {
            this.path = ConfEnv.getInstance().getHome();
            this.url = ConfEnv.getInstance().getContextPath(this.path);
        }else {
            this.path = path;
            this.url = ConfEnv.getInstance().getContextPath(this.path);
        }
    }

    public void init(final Properties properties) {
        if(properties != null && !properties.isEmpty()) {
            this.properties = properties;
        }else {
            this.properties = new Properties();
        }
    }

    public String getName() {
        return path.substring(path.lastIndexOf("/") + 1);
    }

    public String getTitle() {
        return properties.getOr("title", getName());
    }

    public String getSubTitle() {
        return properties.getOr("subTitle", "");
    }

    public String getMenuTitle() {
        return properties.getOr("navTitle", "pageTitle", "title", "");
    }

    public String getNavTitle() {
        return properties.getOr("navTitle", "pageTitle", "title", "");
    }

    public String getPageTitle() {
        return properties.getOr("pageTitle", "navTitle", "title", "");
    }

    public String getBrandSlug() {
        return properties.getOr("brandSlug", "");
    }

    public String getHeadline() {
        return properties.getOr("headline", "");
    }

    public String getDescription() {
        return properties.getOr("description", "");
    }

    public String getPath() {
        return properties.getOr("path", "");
    }

    public String getUrl() {
        return properties.getOr("url", "");
    }

    public String getRedirect() {
        return properties.getOr("redirect", "");
    }

    public String getIcon() {
        return properties.getOr("icon", "");
    }

    public String getLanguage() {
        return properties.getOr("language", "");
    }

    public String getTemplate() {
        return properties.getOr("template", "page");
    }

    public String getResourceType() {
        return properties.getOr("resourceType", "");
    }

    public int getOrder() {
        return properties.get("order", 0);
    }

    public boolean isRoot() {
        return properties.get("isRoot", false);
    }

    public boolean isHideInNav() {
        return properties.get("isHideInNav", true);
    }

    public boolean isSeo() {
        return properties.get("isSeo", true);
    }

    public boolean isPwa() {
        return properties.get("pwa", true);
    }

    public boolean isCache() {
        return properties.get("isCache", true);
    }

    public boolean isAuthAllow() {
        return properties.get("isAuthAllow", true);
    }

    public boolean isAuthAllowAdmin() {
        return properties.get("authAllowAdmin", true);
    }

    public boolean isAuthAllowUser() {
        return properties.get("authAllowUser", true);
    }

    public boolean isAuthDisableAuth() {
        return properties.get("authDisableAuth", false);
    }

    public String getFullTitle(final Page homePage) {
        if(!homePage.equals(this)) {
            return getTitle() + homePage.getBrandSlug();
        }else {
            return getTitle();
        }
    }

    public String getMenu() {
        return "/ui.page"+ path +"/" + "menu.jsp";
    }

    public String getContent() {
        return "/ui.page"+ path +"/" + "content.jsp";
    }

    public String getTemplatePath() {
        return "/WEB-INF/ui.template/"+ getTemplate() +"/.jsp";
    }

    public ArrayList<Page> listChildren() {
        return new PageManager().getListPages(this);
    }

    public boolean isAuthAllow(final boolean isAdmins, final boolean isUsers, final boolean isAuths) {
        if(isAuths && isAuthDisableAuth()) return false;
        if(isUsers && !isAuthAllowUser()) return false;
        if(isAdmins && !isAuthAllowAdmin()) return false;
        return true;
    }

    public boolean exists() {
        return getResource().exists();
    }

    public Resource getResource() {
        return ResourceResolver.getFilePage(path);
    }

    public Page getParent() {
        Resource resourceParent = getResource().getParent();
        return new Page(resourceParent);
    }

    public ArrayList<Properties> getContents() {
        final ArrayList<Properties> listContents = properties.getChildren("content");
        if(listContents.isEmpty() || (listContents.size() == 1 && UtilsText.isEmpty(listContents.get(0).getResourceType()))) {
            final Properties properties = new Properties(new Data()
                    .put("resourceType", getContent())
            );
            listContents.add(properties);
        }
        return listContents;
    }

    public boolean cache() {
        return PageManager.getInstance().cache(this);
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
