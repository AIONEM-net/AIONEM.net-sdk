package aionem.net.sdk.web.jsp.modals;

import aionem.net.sdk.core.utils.AlnUtilsText;
import aionem.net.sdk.web.jsp.AlnJsp;
import lombok.Data;
import lombok.extern.log4j.Log4j2;

import java.util.ArrayList;
import java.util.Objects;


@Log4j2
public @Data class AlnJspPage {

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
    private AlnJspProperties properties = new AlnJspProperties();

    public AlnJspPage() {

    }
    public AlnJspPage(final AlnJsp alnJsp) {
        init(alnJsp);
    }
    public AlnJspPage(final AlnJsp alnJsp, final String path) {
        init(alnJsp, path);
    }
    public AlnJspPage(final AlnJsp alnJsp, final String path, final AlnJspProperties properties) {
        this.properties = properties;
        init(alnJsp, path);
    }
    public AlnJspPage(final AlnJsp alnJsp, final String title, final String path, final String icon) {
        this.title = title;
        this.subTitle = title;
        this.navTitle = title;
        this.pageTitle = title;
        this.icon = icon;
        setPath(alnJsp, path);
    }

    public void setTitles(final String title) {
        this.title = title;
        this.subTitle = title;
        this.navTitle = title;
        this.pageTitle = title;
    }

    public void setPath(final AlnJsp alnJsp, final String path) {
        this.path = alnJsp.getRelativePath(path);
        this.url = alnJsp.getContextPath(this.path);
    }

    public String getNavTitle() {
        return AlnUtilsText.notEmpty(navTitle, pageTitle, title);
    }

    public void setPathUrl(final AlnJsp alnJsp) {
        this.path = alnJsp.getServletPath();
        this.url = alnJsp.getContextServletPath();
    }
    public void setPathUrl(final AlnJsp alnJsp, final String path) {
        this.path = path;
        this.url = alnJsp.getContextPath(path);
    }

    public void init(final AlnJsp alnJsp) {
        setPathUrl(alnJsp);
        init(alnJsp, this.path);
        from(alnJsp.getPage());
    }
    public void init(final AlnJsp alnJsp, final String path) {
        setPathUrl(alnJsp, path);
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
        title = AlnUtilsText.capitalizeFirstLetter(title);
        setTitles(title);
    }

    public void from(final AlnJspPage pageItem) {
        if(isRoot || AlnUtilsText.isEmpty(this.title)) {
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

    public void from(final AlnJspProperties properties) {
        if(isRoot || AlnUtilsText.isEmpty(this.title)) {

        }
    }

    public String getMenuTitle() {
        return AlnUtilsText.notEmpty(pageTitle, navTitle, title);
    }

    public String getFullTitle(final AlnJspPage homePage) {
        if(!homePage.equals(this)) {
            return getTitle() + homePage.getBrandSlug();
        }else {
            return getTitle();
        }
    }

    public ArrayList<AlnJspPage> listChildren(final AlnJsp alnJsp) {
        return alnJsp.getListPages(this);
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
        AlnJspPage that = (AlnJspPage) object;
        return Objects.equals(path, that.path);
    }

    @Override
    public int hashCode() {
        return Objects.hash(path);
    }

}
