package aionem.net.sdk.web.modals;

import aionem.net.sdk.web.WebContext;
import aionem.net.sdk.web.beans.Page;
import aionem.net.sdk.web.beans.Properties;
import aionem.net.sdk.web.dao.PageManager;
import lombok.Getter;

import java.util.ArrayList;


@Getter
public class CmpMenu extends Component {

    public final ArrayList<Page> listPageItems = new ArrayList<>();

    public CmpMenu() {
        init(this);
    }

    public CmpMenu(final WebContext webContext) {
        init(this, webContext);
    }

    public CmpMenu(final WebContext webContext, final Properties properties) {
        init(this, webContext, properties);
    }

    @Override
    protected void init() {

    }

    public ArrayList<Page> getListPageItems() {
        if(listPageItems.isEmpty()) {
            final PageManager pageManager = PageManager.getInstance();
            for(final Page page : pageManager.getListPages(webContext.getHomePage())) {
                if(!page.isHideInNav()) {
                    listPageItems.add(page);
                }
            }
        }
        return listPageItems;
    }

    public void add(final Page pageItem) {
        listPageItems.add(pageItem);
    }

    public void add(final Page pageItem, final boolean isAdmins, final boolean isUsers, final boolean isAuth) {
        if(pageItem.isAuthAllow(isAdmins, isUsers, isAuth)) {
            add(pageItem);
        }
    }

}
