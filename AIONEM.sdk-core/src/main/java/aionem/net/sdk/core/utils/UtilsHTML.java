package aionem.net.sdk.core.utils;


public class UtilsHTML {

    public static String toHtml(final String value) {
        String html = UtilsText.notNull(value);
        html = html.replace("\n", "<br>");
        return html;
    }

}
