package br.com.eboli.utils;

public class StringUtil {

    public static String replaceToSpace(String param) {
        return param.replaceAll("_", " ");
    }

    public static String replaceToUnderscore(String param) {
        return param.replaceAll(" ", "_");
    }

}
