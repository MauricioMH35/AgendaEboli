package br.com.eboli.utils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DateFormatter {

    public static LocalDateTime parseDateTime(String dateTime) {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy.MM.dd HH-mm-ss");
        return LocalDateTime.parse(dateTime, fmt);
    }

    public static String parseDateTime(LocalDateTime dateTime) {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy.MM.dd HH-mm-ss");
        return dateTime.format(fmt);
    }

    public static  Boolean checkDatePattern(String target) {
        final String regex = "(\\d{4}.\\d{2}.\\d{2})";
        final Pattern pattern = Pattern.compile(regex, Pattern.MULTILINE);
        final Matcher matcher = pattern.matcher(target);
        return matcher.find();
    }

    public static Boolean checkDateTimePattern(String target) {
        final String regex = "(\\d{4}.\\d{2}.\\d{2})_(\\d{2}-\\d{2}-\\d{2})";
        final Pattern pattern = Pattern.compile(regex, Pattern.MULTILINE);
        final Matcher matcher = pattern.matcher(target);
        return matcher.find();
    }

    public static LocalDate parseDate(String date) {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy.MM.dd");
        return LocalDate.parse(date, fmt);
    }

    public static String parseDate(LocalDate date) {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy.MM.dd");
        return date.format(fmt);
    }

}
