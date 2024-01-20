package aionem.net.sdk.web.modals;

import aionem.net.sdk.web.WebContext;
import aionem.net.sdk.web.beans.Properties;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;


@Log4j2
public @Getter class CmpPage extends Component {

    public CmpPage() {
        init(this);
    }

    public CmpPage(final WebContext webContext) {
        init(this, webContext);
    }

    public CmpPage(final WebContext webContext, final Properties properties) {
        init(this, webContext, properties);
    }

    @Override
    protected void init() {
        
    }

}
