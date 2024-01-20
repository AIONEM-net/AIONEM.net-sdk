package aionem.net.sdk.web.dao;

import aionem.net.sdk.data.beans.Data;
import aionem.net.sdk.web.WebContext;
import aionem.net.sdk.web.config.Conf;

import javax.servlet.http.HttpServletRequest;
import java.util.Base64;
import java.util.StringTokenizer;


public class DaoWebAuth {

    private static DaoWebAuth daoWebAuth;

    public final Conf confUser;

    public static DaoWebAuth getInstance() {
        if(daoWebAuth == null) {
            daoWebAuth = new DaoWebAuth();
        }
        return daoWebAuth;
    }

    private DaoWebAuth() {
        confUser = new Conf("users");
    }

    public Data getUser(final String email) {
        final Data dataUser = confUser.getChild(email);
        dataUser.remove("password");
        return dataUser;
    }

    public boolean hasUser(final String email) {
        return !confUser.isEmpty(email);
    }

    public boolean isAuthenticated(final String email, final String password) {
        final Data dataUser = confUser.getChild(email);
        if(dataUser.equals2("password", password)) {
            return true;
        }
        return false;
    }

    public boolean isAuthenticated(final WebContext webContext) {
        return isAuthenticated(webContext.getRequest());
    }

    public boolean isAuthenticated(final HttpServletRequest request) {
        final String authorization = request.getHeader("Authorization");

        if(authorization != null && authorization.startsWith("Basic")) {

            final String base64Credentials = authorization.substring("Basic".length()).trim();
            final String credentials = new String(Base64.getDecoder().decode(base64Credentials));

            final StringTokenizer tokenizer = new StringTokenizer(credentials, ":");
            String email = tokenizer.nextToken();
            String password = tokenizer.nextToken();

            return isAuthenticated(email, password);
        }

        return false;
    }

}
