package aionem.net.sdk.web.modals;

import aionem.net.sdk.web.AioWeb;
import aionem.net.sdk.web.beans.Properties;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;


@Log4j2
public @Getter class CmpPage extends Component {

    public CmpPage() {
        init(this);
    }

    public CmpPage(final AioWeb aioWeb) {
        init(this, aioWeb);
    }

    public CmpPage(final AioWeb aioWeb, final Properties properties) {
        init(this, aioWeb, properties);
    }

    @Override
    protected void init() {
        
    }

}
