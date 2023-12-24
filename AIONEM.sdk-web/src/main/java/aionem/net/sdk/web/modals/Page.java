package aionem.net.sdk.web.modals;

import aionem.net.sdk.core.utils.UtilsText;
import aionem.net.sdk.web.AioWeb;
import lombok.Data;
import lombok.extern.log4j.Log4j2;

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
    private String template = "";
    private String resourceType = "";
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

    public Page(final AioWeb aioWeb, final String path) {
        init(aioWeb, path);
    }

    public Page(final AioWeb aioWeb, final String path, final Properties properties) {
        this.properties = properties;
        init(aioWeb, path);
    }

    public Page(final AioWeb aioWeb, final String title, final String path, final String icon) {
        this.title = title;
        this.subTitle = title;
        this.navTitle = title;
        this.pageTitle = title;
        this.icon = icon;
        setPath(aioWeb, path);
    }

    public void setTitles(final String title) {
        this.title = title;
        this.subTitle = title;
        this.navTitle = title;
        this.pageTitle = title;
    }

    public void setPath(final AioWeb aioWeb, final String path) {
        this.path = aioWeb.getRelativePath(path);
        this.url = aioWeb.getContextPath(this.path);
    }

    public String getNavTitle() {
        return UtilsText.notEmpty(navTitle, pageTitle, title);
    }

    public void setPathUrl(final AioWeb aioWeb) {
        this.path = aioWeb.getServletPath();
        this.url = aioWeb.getContextServletPath();
    }
    public void setPathUrl(final AioWeb aioWeb, final String path) {
        this.path = path;
        this.url = aioWeb.getContextPath(path);
    }

    public void init(final AioWeb aioWeb) {
        setPathUrl(aioWeb);
        init(aioWeb, this.path);
        from(new PageManager(aioWeb).getPage());
    }

    public void init(final AioWeb aioWeb, final String path) {
        setPathUrl(aioWeb, path);
        String title = this.path;
        title = title
                .replace("/index.jsp", "")
                .replace("/index.html", "")
                .replace(".jsp", "")
                .replace(".html", "")
                .replace("-", " ")
                .replace("_", " ")
                .replace("//", "");
        final int index1 = title.lastIndexOf("/");
        if(index1 >= 0) {
            title = title.substring(index1 + 1);
        }
        title = UtilsText.capitalizeFirstLetter(title);
        setTitles(title);
    }

    public void from(final Page pageItem) {
        if(isRoot || UtilsText.isEmpty(this.title)) {
            this.title = pageItem.getTitle();
            this.navTitle = pageItem.getNavTitle();
            this.pageTitle = pageItem.getPageTitle();
            this.brandSlug = pageItem.getBrandSlug();
            this.path = pageItem.getPath();
            this.url = pageItem.getUrl();
            this.icon = pageItem.getIcon();
            this.properties = pageItem.getProperties();
        }
    }

    public void from(final Properties properties) {
        if(isRoot || UtilsText.isEmpty(this.title)) {

        }
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

    public ArrayList<Page> listChildren(final AioWeb aioWeb) {
        return new PageManager(aioWeb).getListPages(this);
    }

    public boolean isAuthAllow(final boolean isAdmins, final boolean isUsers, final boolean isAuths) {
        if(isAuths && authDisableAuth) return false;
        if(isUsers && !authAllowUser) return false;
        if(isAdmins && !authAllowAdmin) return false;
        return true;
    }

    @Override
    public String toString() {
        return properties.toString();
    }

    @Override
    public boolean equals(Object object) {
        if(this == object) return true;
        if(object == null || getClass() != object.getClass()) return false;
        Page that = (Page) object;
        return Objects.equals(path, that.path);
    }

    @Override
    public int hashCode() {
        return Objects.hash(path);
    }

}
