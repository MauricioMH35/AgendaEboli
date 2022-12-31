package br.com.eboli.utils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DateUtil {

    private static final DateTimeFormatter fmtDate = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter fmtDateTime = DateTimeFormatter.ofPattern("yyy-MM-dd_HH-mm-ss");

    private static final String ptnDate = "(\\d{4})[-]{1}(\\d{2})[-]{1}(\\d{2})";
    private static final String ptnTime = "(\\d{2})[-]{1}(\\d{2})[-]{1}(\\d{2})";
    private static final String ptnDateTime = ptnDate+"[_]{1}"+ptnTime;

    public static LocalDate parseDate(String date) {
        return LocalDate.parse(date, fmtDate);
    }

    public static String parseDate(LocalDate date) {
        return date.format(fmtDate);
    }

    public static LocalDateTime parseDateTime(String dateTime) {
        return LocalDateTime.parse(dateTime, fmtDateTime);
    }

    public static String parseDateTime(LocalDateTime dateTime) {
        return dateTime.format(fmtDateTime);
    }

    public static Boolean isDate(String date) {
        final Pattern pattern = Pattern.compile(ptnDate, Pattern.MULTILINE);
        final Matcher matcher = pattern.matcher(date);
        boolean valid = matcher.find();
        return valid;
    }

    public static Boolean isDateTime(String dateTime) {
        final Pattern pattern = Pattern.compile(ptnDateTime, Pattern.MULTILINE);
        final Matcher matcher = pattern.matcher(dateTime);
        boolean valid = matcher.find();
        return valid;
    }

}
