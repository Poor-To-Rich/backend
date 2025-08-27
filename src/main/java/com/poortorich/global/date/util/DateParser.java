package com.poortorich.global.date.util;

import com.poortorich.global.date.constants.DatePattern;
import com.poortorich.global.date.response.enums.DateResponse;
import com.poortorich.global.exceptions.BadRequestException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class DateParser {

    public static LocalDate parseDate(String date) {
        if (date == null || date.isBlank()) {
            return LocalDate.now();
        }

        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DatePattern.LOCAL_DATE_PATTERN);
            return LocalDate.parse(date, formatter);
        } catch (DateTimeParseException e) {
            throw new BadRequestException(DateResponse.UNSUPPORTED_DATE_FORMAT);
        }
    }

    public static LocalDateTime parseDateTime(String date) {
        if (date == null || date.isBlank()) {
            return LocalDateTime.now();
        }

        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DatePattern.LOCAL_DATE_TIME_PATTERN);
            return LocalDateTime.parse(date, formatter);
        } catch (DateTimeParseException e) {
            throw new BadRequestException(DateResponse.UNSUPPORTED_DATE_FORMAT);
        }
    }
}
