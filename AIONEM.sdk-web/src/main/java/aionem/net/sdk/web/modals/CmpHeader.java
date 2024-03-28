package aionem.net.sdk.web.modals;

import aionem.net.sdk.web.WebContext;
import aionem.net.sdk.web.beans.Properties;
import lombok.Data;
import lombok.EqualsAndHashCode;


@EqualsAndHashCode(callSuper = true)
public @Data class CmpHeader extends Component {

    public CmpHeader() {
        super();
    }

    public CmpHeader(final WebContext webContext) {
        init(this, webContext);
    }

    public CmpHeader(final WebContext webContext, final Properties properties) {
        init(this, webContext, properties);
    }

    @Override
    protected void init() {
        
    }

}
