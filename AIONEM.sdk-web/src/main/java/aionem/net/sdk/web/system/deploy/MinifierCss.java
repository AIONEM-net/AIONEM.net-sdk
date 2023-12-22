package aionem.net.sdk.web.system.deploy;

import aionem.net.sdk.data.api.DaoRes;
import aionem.net.sdk.core.utils.UtilsText;
import aionem.net.sdk.web.AioWeb;
import aionem.net.sdk.web.utils.UtilsWeb;
import lombok.extern.log4j.Log4j2;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@Log4j2
public class MinifierCss {


    public static DaoRes minifySave(String pathInOut) {
        return minifySave(pathInOut, pathInOut);
    }
    public static DaoRes minifySave(String pathIn, String pathOut) {

        final DaoRes resMinify = new DaoRes();

        final File fileIn = new File(pathIn);
        final String css = minify(UtilsText.toString(fileIn));

        final File fileOut = new File(pathOut);
        final boolean isSaved = UtilsWeb.writeFile(fileOut, css);

        resMinify.setSuccess(isSaved);

        return resMinify;
    }

    public static String minifyFolder(final AioWeb aioWeb, final File fileFolder, final boolean isSave) {

        final StringBuilder builderCss = new StringBuilder();

        final String uiFrontend = aioWeb.getContextPath("/ui.frontend");

        final File fileCss = new File(fileFolder, ".css");
        final File fileCssJsp = new File(fileFolder, "css.jsp");
        final Pattern pattern = Pattern.compile("(/ui\\.frontend[^\"']*\\.css)\"");

        final Matcher matcher = pattern.matcher(UtilsText.toString(fileCssJsp));

        final ArrayList<String> listFileCss = new ArrayList<>();
        while(matcher.find()) {
            listFileCss.add(matcher.group(1));
        }
        for(int i = 0; i < listFileCss.size(); i++) {

            final File file = new File(aioWeb.getRealPathRoot(listFileCss.get(i)));

            String css = UtilsText.toString(file);

            if(!file.getName().equals("min.css")) {
                css = minifyFile(file);
            }

            if(isSave) {
                UtilsWeb.writeFile(file, css);
            }

            css = css
                    .replace("(/ui.frontend", "("+ uiFrontend)
                    .replace("\"/ui.frontend", "\""+ uiFrontend)
                    .replace("'/ui.frontend", "'"+ uiFrontend)
                    .replace("`/ui.frontend", "`"+ uiFrontend)
                    .replace("../", "");

            if(!UtilsText.isEmpty(css)) {
                builderCss.append(i > 0 ? "\n" : "").append(css);
            }

        }
        if(isSave) {
            UtilsWeb.writeFile(fileCss, builderCss.toString());
        }

        if(isSave) {

            final FilenameFilter filterCss = new FilenameFilter() {
                @Override
                public boolean accept(final File file, final String name) {
                    return name.toLowerCase().endsWith(".css");
                }
            };

            final ArrayList<File> listFiles = UtilsWeb.findFiles(fileFolder, filterCss);

            for(int i = 0; i < listFiles.size(); i++) {

                final File file = listFiles.get(i);

                if(file.isFile()) {

                    String css = UtilsText.toString(file);

                    if(!file.getName().equals("min.css")) {
                        css = minifyFile(file);
                    }

                    UtilsWeb.writeFile(file, css);
                }
            }
        }

        return builderCss.toString();
    }

    public static String minifyFile(File file) {
        return minify(UtilsText.toString(file));
    }

    public static String minify(String css) {

        try {

            int k, j, n;
            char curr;

            StringBuilder sb = new StringBuilder(css);

            n = 0;
            while((n = sb.indexOf("/*", n)) != -1) {
                if(sb.charAt(n + 2) == '*') {
                    n += 2;
                    continue;
                }
                k = sb.indexOf("*/", n + 2);
                if(k == -1) {
                    throw new Exception("UnterminatedCommentException");
                }
                sb.delete(n, k + 2);
            }

            List<JspMinifierCssUtils.Selector> selectors = new ArrayList<>();
            n = 0;
            j = 0;
            for (int i = 0; i < sb.length(); i++) {
                curr = sb.charAt(i);
                if(j < 0) {
                    throw new Exception("UnbalancedBracesException");
                }
                if(curr == '{') {
                    j++;
                } else if(curr == '}') {
                    j--;
                    if(j == 0) {
                        try {
                            selectors.add(new JspMinifierCssUtils.Selector(sb.substring(n, i + 1)));
                        } catch (Exception ignore) {
                        }
                        n = i + 1;
                    }
                }
            }

            StringBuilder cssBuilder = new StringBuilder();
            for(JspMinifierCssUtils.Selector selector : selectors) {
                cssBuilder.append(selector.toString());
            }

            css = cssBuilder.toString();

        }catch (Exception e) {
            log.error("Minification failed", e);
        }

        return css;
    }

    private static class JspMinifierCssUtils {

        private static final String[] HTML_COLOUR_NAMES = { "aliceblue", "antiquewhite", "aqua", "aquamarine", "azure", "beige",
                "bisque", "black", "blanchedalmond", "blue", "blueviolet", "brown", "burlywood", "cadetblue", "chartreuse",
                "chocolate", "coral", "cornflowerblue", "cornsilk", "crimson", "cyan", "darkblue", "darkcyan",
                "darkgoldenrod", "darkgray", "darkgreen", "darkkhaki", "darkmagenta", "darkolivegreen", "darkorange",
                "darkorchid", "darkred", "darksalmon", "darkseagreen", "darkslateblue", "darkslategray", "darkturquoise",
                "darkviolet", "deeppink", "deepskyblue", "dimgray", "dodgerblue", "firebrick", "floralwhite", "forestgreen",
                "fuchsia", "gainsboro", "ghostwhite", "gold", "goldenrod", "gray", "green", "greenyellow", "honeydew",
                "hotpink", "indianred ", "indigo ", "ivory", "khaki", "lavender", "lavenderblush", "lawngreen",
                "lemonchiffon", "lightblue", "lightcoral", "lightcyan", "lightgoldenrodyellow", "lightgrey", "lightgreen",
                "lightpink", "lightsalmon", "lightseagreen", "lightskyblue", "lightslategray", "lightsteelblue",
                "lightyellow", "lime", "limegreen", "linen", "magenta", "maroon", "mediumaquamarine", "mediumblue",
                "mediumorchid", "mediumpurple", "mediumseagreen", "mediumslateblue", "mediumspringgreen", "mediumturquoise",
                "mediumvioletred", "midnightblue", "mintcream", "mistyrose", "moccasin", "navajowhite", "navy", "oldlace",
                "olive", "olivedrab", "orange", "orangered", "orchid", "palegoldenrod", "palegreen", "paleturquoise",
                "palevioletred", "papayawhip", "peachpuff", "peru", "pink", "plum", "powderblue", "purple", "red",
                "rosybrown", "royalblue", "saddlebrown", "salmon", "sandybrown", "seagreen", "seashell", "sienna", "silver",
                "skyblue", "slateblue", "slategray", "snow", "springgreen", "steelblue", "tan", "teal", "thistle", "tomato",
                "turquoise", "violet", "wheat", "white", "whitesmoke", "yellow", "yellowgreen" };
        private static final String[] HTML_COLOUR_VALUES = { "#f0f8ff", "#faebd7", "#00ffff", "#7fffd4", "#f0ffff", "#f5f5dc",
                "#ffe4c4", "#000", "#ffebcd", "#00f", "#8a2be2", "#a52a2a", "#deb887", "#5f9ea0", "#7fff00", "#d2691e",
                "#ff7f50", "#6495ed", "#fff8dc", "#dc143c", "#0ff", "#00008b", "#008b8b", "#b8860b", "#a9a9a9", "#006400",
                "#bdb76b", "#8b008b", "#556b2f", "#ff8c00", "#9932cc", "#8b0000", "#e9967a", "#8fbc8f", "#483d8b",
                "#2f4f4f", "#00ced1", "#9400d3", "#ff1493", "#00bfff", "#696969", "#1e90ff", "#b22222", "#fffaf0",
                "#228b22", "#f0f", "#dcdcdc", "#f8f8ff", "#ffd700", "#daa520", "#808080", "#008000", "#adff2f", "#f0fff0",
                "#ff69b4", "#cd5c5c", "#4b0082", "#fffff0", "#f0e68c", "#e6e6fa", "#fff0f5", "#7cfc00", "#fffacd",
                "#add8e6", "#f08080", "#e0ffff", "#fafad2", "#d3d3d3", "#90ee90", "#ffb6c1", "#ffa07a", "#20b2aa",
                "#87cefa", "#789", "#b0c4de", "#ffffe0", "#0f0", "#32cd32", "#faf0e6", "#f0f", "#800000", "#66cdaa",
                "#0000cd", "#ba55d3", "#9370d8", "#3cb371", "#7b68ee", "#00fa9a", "#48d1cc", "#c71585", "#191970",
                "#f5fffa", "#ffe4e1", "#ffe4b5", "#ffdead", "#000080", "#fdf5e6", "#808000", "#6b8e23", "#ffa500",
                "#ff4500", "#da70d6", "#eee8aa", "#98fb98", "#afeeee", "#d87093", "#ffefd5", "#ffdab9", "#cd853f",
                "#ffc0cb", "#dda0dd", "#b0e0e6", "#800080", "#f00", "#bc8f8f", "#4169e1", "#8b4513", "#fa8072", "#f4a460",
                "#2e8b57", "#fff5ee", "#a0522d", "#c0c0c0", "#87ceeb", "#6a5acd", "#708090", "#fffafa", "#00ff7f",
                "#4682b4", "#d2b48c", "#008080", "#d8bfd8", "#ff6347", "#40e0d0", "#ee82ee", "#f5deb3", "#fff", "#f5f5f5",
                "#ff0", "#9acd32" };
        private static final String[] FONT_WEIGHT_NAMES = { "normal", "bold", "bolder", "lighter" };
        private static final String[] FONT_WEIGHT_VALUES = { "400", "700", "900", "100" };

        private static class Selector {
            private Property[] properties = null;
            private List<Selector> subSelectors = null;
            private String selector;

            public Selector(String selector)
                    throws Exception {
                String[] parts = selector.split("\\{");
                if(parts.length < 2) {
                    throw new Exception("IncompleteSelectorException("+ selector +")");
                }

                this.selector = parts[0].trim();

                this.selector = this.selector.replaceAll("\\s?(\\+|~|,|=|~=|\\^=|\\$=|\\*=|\\|=|>)\\s?", "$1");

                if(parts.length > 2) {
                    this.subSelectors = new ArrayList<>();
                    parts = selector.split("(\\s*\\{\\s*)|(\\s*\\}\\s*)");
                    for (int i = 1; i < parts.length; i += 2) {
                        parts[i] = parts[i].trim();
                        parts[i + 1] = parts[i + 1].trim();
                        if(!(parts[i].isEmpty() || (parts[i + 1].isEmpty()))) {
                            this.subSelectors.add(new Selector(parts[i] + "{" + parts[i + 1] + "}"));
                        }
                    }
                } else {
                    String contents = parts[parts.length - 1].trim();
                    log.debug("Parsing selector: {}", this.selector);
                    log.debug("\t{}", contents);
                    if(contents.charAt(contents.length() - 1) != '}') {
                        throw new Exception("UnterminatedSelectorException("+ selector +")");
                    }
                    if(contents.length() == 1) {
                        throw new Exception("EmptySelectorBodyException("+ selector +")");
                    }
                    contents = contents.substring(0, contents.length() - 1);
                    if(contents.charAt(contents.length() - 1) == ';') {
                        contents = contents.substring(0, contents.length() - 1);
                    }

                    this.properties = new Property[0];
                    this.properties = parseProperties(contents).toArray(this.properties);
                    sortProperties(this.properties);
                }
            }

            public String toString() {
                StringBuilder sb = new StringBuilder();
                sb.append(this.selector).append("{");
                if(this.subSelectors != null) {
                    for (Selector s : this.subSelectors) {
                        sb.append(s.toString());
                    }
                }
                if(this.properties != null) {
                    for (Property p : this.properties) {
                        sb.append(p.toString());
                    }
                }
                if(sb.charAt(sb.length() - 1) == ';') {
                    sb.deleteCharAt(sb.length() - 1);
                }
                sb.append("}");
                return sb.toString();
            }

            private ArrayList<Property> parseProperties(String contents) throws Exception {
                List<String> parts = new ArrayList<>();
                boolean bInsideString = false, bInsideURL = false;
                int j = 0;
                String substr;
                for (int i = 0; i < contents.length(); i++) {
                    if(bInsideString) {
                        bInsideString = !(contents.charAt(i) == '"');
                    } else if(bInsideURL) {
                        bInsideURL = !(contents.charAt(i) == ')');
                    } else if(contents.charAt(i) == '"') {
                        bInsideString = true;
                    } else if(contents.charAt(i) == '(') {
                        if((i - 3) > 0 && "url".equals(contents.substring(i - 3, i)))
                            bInsideURL = true;
                    } else if(contents.charAt(i) == ';') {
                        substr = contents.substring(j, i);
                        if(!substr.trim().isEmpty()) {
                            parts.add(substr);
                        }
                        j = i + 1;
                    }
                }
                substr = contents.substring(j);
                if(!substr.trim().isEmpty()) {
                    parts.add(substr);
                }

                ArrayList<Property> results = new ArrayList<>();
                for (int i = 0; i < parts.size(); i++) {
                    try {
                        results.add(new Property(parts.get(i)));
                    } catch (Exception e) {
                        log.debug("Incomplete property in selector '{}': {}", selector, e.getMessage());
                    }
                }

                return results;
            }

            private void sortProperties(Property[] properties) {
                Arrays.sort(properties);
            }
        }

        private static class Property implements Comparable<Property> {
            protected String property;
            protected Part[] parts;

            public Property(String property) throws Exception {
                ArrayList<String> parts = new ArrayList<>();
                boolean bCanSplit = true;
                int j = 0;
                String substr;
                log.debug("\t\tExamining property: {}", property);
                for (int i = 0; i < property.length(); i++) {
                    if(!bCanSplit) {
                        bCanSplit = (property.charAt(i) == '"');
                    } else if(property.charAt(i) == '"') {
                        bCanSplit = false;
                    } else if(property.charAt(i) == ':' && parts.isEmpty()) {
                        substr = property.substring(j, i);
                        if(!substr.trim().isEmpty()) {
                            parts.add(substr);
                        }
                        j = i + 1;
                    }
                }
                substr = property.substring(j);
                if(!substr.trim().isEmpty()) {
                    parts.add(substr);
                }
                if(parts.size() < 2) {
                    throw new Exception("IncompletePropertyException("+ property +")");
                }

                String prop = parts.get(0).trim();
                if(!(prop.length() > 2 && prop.startsWith("--"))) {
                    prop = prop.toLowerCase();
                }
                this.property = prop;
                this.parts = parseValues(simplifyColours(parts.get(1).trim().replaceAll(", ", ",")));
            }

            public String toString() {
                StringBuilder sb = new StringBuilder();
                sb.append(this.property).append(":");
                for (Part p : this.parts) {
                    sb.append(p.toString()).append(",");
                }
                sb.deleteCharAt(sb.length() - 1); // Delete the trailing comma.
                sb.append(";");
                return sb.toString();
            }

            public int compareTo(Property other) {
                String thisProp = this.property;
                String thatProp = other.property;

                if(thisProp.charAt(0) == '-') {
                    thisProp = thisProp.substring(1);
                    thisProp = thisProp.substring(thisProp.indexOf('-') + 1);
                } else if(thisProp.charAt(0) < 65) {
                    thisProp = thisProp.substring(1);
                }

                if(thatProp.charAt(0) == '-') {
                    thatProp = thatProp.substring(1);
                    thatProp = thatProp.substring(thatProp.indexOf('-') + 1);
                } else if(thatProp.charAt(0) < 65) {
                    thatProp = thatProp.substring(1);
                }

                return thisProp.compareTo(thatProp);
            }

            private Part[] parseValues(String contents) {
                String[] parts = contents.split(",");
                Part[] results = new Part[parts.length];

                for (int i = 0; i < parts.length; i++) {
                    try {
                        results[i] = new Part(parts[i], property);
                    } catch (Exception e) {
                        log.debug("Exception in parseValues().", e);
                        results[i] = null;
                    }
                }

                return results;
            }

            private String simplifyColours(String contents) {
                return simplifyRGBColours(contents);
            }

            private String simplifyRGBColours(String contents) {
                StringBuilder newContents = new StringBuilder();
                StringBuilder hexColour;
                String[] rgbColours;
                int colourValue;

                Pattern pattern = Pattern.compile("rgb\\s*\\(\\s*([0-9,\\s]+)\\s*\\)");
                Matcher matcher = pattern.matcher(contents);

                while(matcher.find()) {
                    hexColour = new StringBuilder("#");
                    rgbColours = matcher.group(1).split(",");
                    for (int i = 0; i < rgbColours.length; i++) {
                        colourValue = Integer.parseInt(rgbColours[i]);
                        if(colourValue < 16) {
                            hexColour.append("0");
                        }
                        hexColour.append(Integer.toHexString(colourValue));
                    }
                    matcher.appendReplacement(newContents, hexColour.toString());
                }
                matcher.appendTail(newContents);

                return newContents.toString();
            }
        }

        private static class Part {
            String contents;
            String property;

            public Part(String contents, String property) {
                this.contents = " " + contents;
                this.property = property;
                simplify();
            }

            private void simplify() {

                this.contents = this.contents.replaceAll(" !important", "!important");

                this.contents = this.contents.replaceAll("(\\s)(0)(px|em|%|in|cm|mm|pc|pt|ex)", "$1$2");

                this.contents = this.contents.trim();

                if(this.contents.equals("0 0 0 0")) {
                    this.contents = "0";
                }
                if(this.contents.equals("0 0 0")) {
                    this.contents = "0";
                }
                if(this.contents.equals("0 0")) {
                    this.contents = "0";
                }

                simplifyParameters();

                simplifyFontWeights();

                simplifyQuotesAndCaps();

                simplifyColourNames();
                simplifyHexColours();
            }

            private void simplifyParameters() {
                if(this.property.equals("background-size") || this.property.equals("quotes")
                        || this.property.equals("transform-origin"))
                    return;

                StringBuilder newContents = new StringBuilder();

                String[] params = this.contents.split(" ");
                if(params.length == 4) {
                    if(params[1].equalsIgnoreCase(params[3])) {
                        params = Arrays.copyOf(params, 3);
                    }
                }
                if(params.length == 3) {
                    if(params[0].equalsIgnoreCase(params[2])) {
                        params = Arrays.copyOf(params, 2);
                    }
                }
                if(params.length == 2) {
                    if(params[0].equalsIgnoreCase(params[1])) {
                        params = Arrays.copyOf(params, 1);
                    }
                }

                for (int i = 0; i < params.length; i++) {
                    newContents.append(params[i]).append(" ");
                }
                newContents.deleteCharAt(newContents.length() - 1);

                this.contents = newContents.toString();
            }

            private void simplifyFontWeights() {
                if(!this.property.equals("font-weight"))
                    return;

                String lcContents = this.contents.toLowerCase();

                for (int i = 0; i < FONT_WEIGHT_NAMES.length; i++) {
                    if(lcContents.equals(FONT_WEIGHT_NAMES[i])) {
                        this.contents = FONT_WEIGHT_VALUES[i];
                        break;
                    }
                }
            }

            private void simplifyQuotesAndCaps() {
                if((this.contents.length() > 4) && (this.contents.substring(0, 4).equalsIgnoreCase("url("))) {
                    this.contents = this.contents.replaceAll("(?i)url\\(('|\")?(.*?)\\1\\)", "url($2)");
                } else if((this.contents.length() > 4) && (this.contents.substring(0, 4).equalsIgnoreCase("var("))) {
                    this.contents = this.contents.replaceAll("\\s", "");
                } else {
                    String[] words = this.contents.split("\\s");
                    if(words.length == 1) {
                        if(!this.property.equalsIgnoreCase("animation-name")) {
                            this.contents = this.contents.toLowerCase();
                        }
                        this.contents = this.contents.replaceAll("('|\")?(.*?)\1", "$2");
                    }
                }
            }

            private void simplifyColourNames() {
                String lcContents = this.contents.toLowerCase();

                for (int i = 0; i < HTML_COLOUR_NAMES.length; i++) {
                    if(lcContents.equals(HTML_COLOUR_NAMES[i])) {
                        if(HTML_COLOUR_VALUES[i].length() < HTML_COLOUR_NAMES[i].length()) {
                            this.contents = HTML_COLOUR_VALUES[i];
                        }
                        break;
                    } else if(lcContents.equals(HTML_COLOUR_VALUES[i])) {
                        if(HTML_COLOUR_NAMES[i].length() < HTML_COLOUR_VALUES[i].length()) {
                            this.contents = HTML_COLOUR_NAMES[i];
                        }
                    }
                }
            }

            private void simplifyHexColours() {
                StringBuilder newContents = new StringBuilder();

                Pattern pattern = Pattern.compile("#([0-9a-fA-F])([0-9a-fA-F])([0-9a-fA-F])([0-9a-fA-F])([0-9a-fA-F])([0-9a-fA-F])");
                Matcher matcher = pattern.matcher(this.contents);

                while(matcher.find()) {
                    if(matcher.group(1).equalsIgnoreCase(matcher.group(2))
                            && matcher.group(3).equalsIgnoreCase(matcher.group(4))
                            && matcher.group(5).equalsIgnoreCase(matcher.group(6))) {
                        matcher.appendReplacement(newContents, "#" + matcher.group(1).toLowerCase()
                                + matcher.group(3).toLowerCase() + matcher.group(5).toLowerCase());
                    } else {
                        matcher.appendReplacement(newContents, matcher.group().toLowerCase());
                    }
                }
                matcher.appendTail(newContents);

                this.contents = newContents.toString();
            }

            public String toString() {
                return this.contents;
            }
        }

    }

}
