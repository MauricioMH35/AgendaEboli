package br.com.eboli.utils;

public class StringFormatter {

    public static String replaceUnderscoreBySpace(String target) {
        return target.replaceAll("_", " ");
    }

    public static String replaceWhiteSpaceByUnderscore(String target) {
        return target.replaceAll(" ", "_");
    }

}
