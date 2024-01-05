package aionem.net.sdk.web.modals;

import aionem.net.sdk.core.utils.UtilsText;
import aionem.net.sdk.web.AioWeb;
import lombok.Getter;

import java.util.ArrayList;


@Getter
public class CmpBreadcrumb extends Component {

    private final ArrayList<Page> listBreadcrumbs = new ArrayList<>();

    public CmpBreadcrumb() {
        init(this);
    }

    public CmpBreadcrumb(final AioWeb aioWeb) {
        init(this, aioWeb);
    }

    public CmpBreadcrumb(final AioWeb aioWeb, final Properties properties) {
        init(this, aioWeb, properties);
    }

    @Override
    protected void init() {

    }

    public ArrayList<Page> init(final Page homePageItem, final Page currentPageItem) {
        listBreadcrumbs.clear();
        listBreadcrumbs.add(homePageItem);
        if(!homePageItem.equals(currentPageItem)) {
            final String[] breadcrumbs = pathDifference(homePageItem, currentPageItem).split("/");
            for(int i = 1; i < breadcrumbs.length - 1; i++) {
                String breadcrumb = breadcrumbs[i];
                final Page pageItem = new Page();
                pageItem.init(listBreadcrumbs.get(i - 1).getPath() + "/" + breadcrumb);
                listBreadcrumbs.add(pageItem);
            }
            listBreadcrumbs.add(currentPageItem);
        }
        return listBreadcrumbs;
    }

    public String pathDifference(final Page pageItem1, Page pageItem2) {
        return pathDifference(pageItem1.getPath(), pageItem2.getPath());
    }

    public String pathDifference(String path1, String path2) {
        path1 = formatPath(path1);
        path2 = formatPath(path2);
        if(path1.length() > path2.length()) {
            return path1.replace(path2, "");
        }else {
            return path2.replace(path1, "");
        }
    }

    public String formatPath(String path) {
        if(!UtilsText.isEmpty(path)) {
            path = path.replaceAll("(?<=://)|(?<=\\S)//", "/");
            if(path.endsWith("/")) {
                path = path.replaceAll("/$", "");
            }
        }
        return path;
    }

}
