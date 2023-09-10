package aionem.net.sdk.utils;


public class AlnHTMLUtils {

    public static String toHtml(final String value) {
        String html = AlnTextUtils.notNull(value);
        html = html.replace("\n", "<br>");
        return html;
    }

}
