package aionem.net.sdk.web.jsp.modals;

import aionem.net.sdk.core.utils.UtilsText;
import aionem.net.sdk.web.jsp.AioJsp;
import lombok.Data;
import lombok.extern.log4j.Log4j2;

import java.util.ArrayList;
import java.util.Objects;


@Log4j2
public @Data class JspPage {

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
    private JspProperties properties = new JspProperties();

    public JspPage() {

    }
    public JspPage(final AioJsp aioJsp) {
        init(aioJsp);
    }
    public JspPage(final AioJsp aioJsp, final String path) {
        init(aioJsp, path);
    }
    public JspPage(final AioJsp aioJsp, final String path, final JspProperties properties) {
        this.properties = properties;
        init(aioJsp, path);
    }
    public JspPage(final AioJsp aioJsp, final String title, final String path, final String icon) {
        this.title = title;
        this.subTitle = title;
        this.navTitle = title;
        this.pageTitle = title;
        this.icon = icon;
        setPath(aioJsp, path);
    }

    public void setTitles(final String title) {
        this.title = title;
        this.subTitle = title;
        this.navTitle = title;
        this.pageTitle = title;
    }

    public void setPath(final AioJsp aioJsp, final String path) {
        this.path = aioJsp.getRelativePath(path);
        this.url = aioJsp.getContextPath(this.path);
    }

    public String getNavTitle() {
        return UtilsText.notEmpty(navTitle, pageTitle, title);
    }

    public void setPathUrl(final AioJsp aioJsp) {
        this.path = aioJsp.getServletPath();
        this.url = aioJsp.getContextServletPath();
    }
    public void setPathUrl(final AioJsp aioJsp, final String path) {
        this.path = path;
        this.url = aioJsp.getContextPath(path);
    }

    public void init(final AioJsp aioJsp) {
        setPathUrl(aioJsp);
        init(aioJsp, this.path);
        from(aioJsp.getPage());
    }
    public void init(final AioJsp aioJsp, final String path) {
        setPathUrl(aioJsp, path);
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

    public void from(final JspPage pageItem) {
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

    public void from(final JspProperties properties) {
        if(isRoot || UtilsText.isEmpty(this.title)) {

        }
    }

    public String getMenuTitle() {
        return UtilsText.notEmpty(pageTitle, navTitle, title);
    }

    public String getFullTitle(final JspPage homePage) {
        if(!homePage.equals(this)) {
            return getTitle() + homePage.getBrandSlug();
        }else {
            return getTitle();
        }
    }

    public ArrayList<JspPage> listChildren(final AioJsp aioJsp) {
        return aioJsp.getListPages(this);
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
        JspPage that = (JspPage) object;
        return Objects.equals(path, that.path);
    }

    @Override
    public int hashCode() {
        return Objects.hash(path);
    }

}
