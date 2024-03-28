package aionem.net.sdk.web.modals;

import aionem.net.sdk.web.WebContext;
import aionem.net.sdk.web.beans.Properties;
import lombok.Data;
import lombok.EqualsAndHashCode;


@EqualsAndHashCode(callSuper = true)
public @Data class CmpFooter extends Component {

    public CmpFooter() {
        init(this);
    }

    public CmpFooter(final WebContext webContext) {
        init(this, webContext);
    }

    public CmpFooter(final WebContext webContext, final Properties properties) {
        init(this, webContext, properties);
    }

    @Override
    protected void init() {
        
    }

}
