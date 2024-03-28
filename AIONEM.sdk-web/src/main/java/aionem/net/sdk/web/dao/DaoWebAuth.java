package aionem.net.sdk.web.dao;

import aionem.net.sdk.core.utils.UtilsText;
import aionem.net.sdk.data.beans.Data;
import aionem.net.sdk.web.WebContext;
import aionem.net.sdk.web.config.Conf;

import javax.servlet.http.Cookie;
import java.util.Base64;


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
        if(dataUser.equals("password", password)) {
            return true;
        }
        return false;
    }

    public boolean isAuthenticated(final WebContext webContext) {
        final String authorization = webContext.getHeader("Authorization");

        String base64Credentials = "";

        if(!UtilsText.isEmpty(authorization) && authorization.startsWith("Basic")) {
            base64Credentials = authorization.substring("Basic".length()).trim();

            final Cookie cookie = new Cookie("AIONEM.NET_UI.SYSTEM", base64Credentials);
            cookie.setMaxAge(30*60);
            webContext.getResponse().addCookie(cookie);

        }else {
            for(final Cookie cookie : webContext.getRequest().getCookies()) {
                if("AIONEM.NET_UI.SYSTEM".equals(cookie.getName())) {
                    base64Credentials = cookie.getValue();
                }
            }
        }

        if(!UtilsText.isEmpty(base64Credentials)) {

            final String[] credentials = new String(Base64.getDecoder().decode(base64Credentials)).split(":");

            final String email = credentials.length > 0 ? credentials[0] : "";
            final String password = credentials.length > 1 ? credentials[1] : "";

            return isAuthenticated(email, password);
        }

        return false;
    }

}
