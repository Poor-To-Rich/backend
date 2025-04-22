package com.poortorich.iteration.request;

import com.poortorich.global.constants.DatePattern;
import com.poortorich.iteration.constants.IterationResponseMessages;
import com.poortorich.iteration.constants.IterationValidationConstraints;
import com.poortorich.iteration.entity.enums.EndType;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
public class End {

    @NotBlank(message = IterationResponseMessages.END_TYPE_REQUIRED)
    private String type;

    @Min(value = IterationValidationConstraints.END_COUNT_MIN,
            message = IterationResponseMessages.END_COUNT_TOO_SMALL)
    @Max(value = IterationValidationConstraints.END_COUNT_MAX,
            message = IterationResponseMessages.END_COUNT_TOO_BIG)
    private Integer count;

    @DateTimeFormat(pattern = DatePattern.BASIC_PATTERN)
    private LocalDate date;

    public EndType parseEndType() {
        return EndType.from(type);
    }
}
