package aionem.net.sdk.web.modals;

import aionem.net.sdk.web.AioWeb;
import aionem.net.sdk.web.beans.Properties;
import lombok.Data;
import lombok.EqualsAndHashCode;


@EqualsAndHashCode(callSuper = true)
public @Data class CmpFooter extends Component {

    public CmpFooter() {
        init(this);
    }

    public CmpFooter(final AioWeb aioWeb) {
        init(this, aioWeb);
    }

    public CmpFooter(final AioWeb aioWeb, final Properties properties) {
        init(this, aioWeb, properties);
    }

    @Override
    protected void init() {
        
    }

}
