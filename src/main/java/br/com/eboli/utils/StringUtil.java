package br.com.eboli.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtil {

    public static String replaceToSpace(String param) {
        return param.replaceAll("_", " ");
    }

    public static String replaceToUnderscore(String param) {
        return param.replaceAll(" ", "_");
    }

    public static Boolean isBooleanValid(String param) {
        final String regex = "(true|TRUE|false|FALSE)";
        final Pattern pattern = Pattern.compile(regex, Pattern.MULTILINE);
        final Matcher matcher = pattern.matcher(param);
        boolean valid = matcher.find();
        return valid;
    }

}
