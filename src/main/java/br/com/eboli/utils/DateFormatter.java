package br.com.eboli.utils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateFormatter {

    public static LocalDateTime parseDateTime(String dateTime) {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy HH.mm.ss");
        return LocalDateTime.parse(dateTime, fmt);
    }

    public static String parseDateTime(LocalDateTime dateTime) {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy HH.mm.ss");
        return dateTime.format(fmt);
    }

    public static LocalDate parseDate(String date) {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        return LocalDate.parse(date, fmt);
    }

    public static String parseDate(LocalDate date) {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        return date.format(fmt);
    }

}
