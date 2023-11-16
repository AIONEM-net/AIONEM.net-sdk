package aionem.net.sdk.core.utils;


public class AlnUtilsHTML {

    public static String toHtml(final String value) {
        String html = AlnUtilsText.notNull(value);
        html = html.replace("\n", "<br>");
        return html;
    }

}
