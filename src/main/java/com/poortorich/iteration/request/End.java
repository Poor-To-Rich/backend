package com.poortorich.iteration.request;

import com.poortorich.global.constants.DatePattern;
import com.poortorich.global.exceptions.BadRequestException;
import com.poortorich.iteration.constants.IterationResponseMessages;
import com.poortorich.iteration.constants.IterationValidationConstraints;
import com.poortorich.iteration.entity.enums.EndType;
import com.poortorich.iteration.response.IterationResponse;
import com.poortorich.iteration.validator.EndCheck;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Getter
@AllArgsConstructor
@EndCheck
public class End {

    private String type;

    @Min(value = IterationValidationConstraints.END_COUNT_MIN,
            message = IterationResponseMessages.END_COUNT_TOO_SMALL)
    @Max(value = IterationValidationConstraints.END_COUNT_MAX,
            message = IterationResponseMessages.END_COUNT_TOO_BIG)
    private Integer count;

    private String date;

    public EndType parseEndType() {
        return EndType.from(type);
    }

    public LocalDate parseDate() {
        try {
            return LocalDate.parse(date, DateTimeFormatter.ofPattern(DatePattern.BASIC_PATTERN));
        } catch (DateTimeException e) {
            throw new BadRequestException(IterationResponse.END_DATE_INVALID);
        }
    }
}
