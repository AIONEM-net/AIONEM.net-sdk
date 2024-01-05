package aionem.net.sdk.web.modals;

import aionem.net.sdk.web.AioWeb;
import lombok.Getter;

import java.util.ArrayList;


@Getter
public class CmpMenu extends Component {

    public final ArrayList<Page> listPageItems = new ArrayList<>();

    public CmpMenu() {
        init(this);
    }

    public CmpMenu(final AioWeb aioWeb) {
        init(this, aioWeb);
    }

    public CmpMenu(final AioWeb aioWeb, final Properties properties) {
        init(this, aioWeb, properties);
    }

    @Override
    protected void init() {

    }

    public void add(final Page pageItem) {
        listPageItems.add(pageItem);
    }

    public void add(final Page pageItem, final boolean isAdmins, final boolean isUsers, final boolean isAuths) {
        if(pageItem.isAuthAllow(isAdmins, isUsers, isAuths)) {
            listPageItems.add(pageItem);
        }
    }

}
