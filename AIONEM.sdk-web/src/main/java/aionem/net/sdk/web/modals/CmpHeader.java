package aionem.net.sdk.web.modals;

import aionem.net.sdk.web.AioWeb;
import aionem.net.sdk.web.beans.Properties;
import lombok.Data;
import lombok.EqualsAndHashCode;


@EqualsAndHashCode(callSuper = true)
public @Data class CmpHeader extends Component {

    public CmpHeader() {
        super();
    }

    public CmpHeader(final AioWeb aioWeb) {
        init(this, aioWeb);
    }

    public CmpHeader(final AioWeb aioWeb, final Properties properties) {
        init(this, aioWeb, properties);
    }

    @Override
    protected void init() {
        
    }

}
