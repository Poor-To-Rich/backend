package com.poortorich.accountbook.request;

import com.poortorich.accountbook.constants.AccountBookResponseMessages;
import com.poortorich.accountbook.constants.AccountBookValidationConstraints;
import com.poortorich.accountbook.entity.enums.IterationType;
import com.poortorich.accountbook.request.enums.IterationAction;
import com.poortorich.accountbook.response.enums.AccountBookResponse;
import com.poortorich.global.date.constants.DatePattern;
import com.poortorich.global.exceptions.BadRequestException;
import com.poortorich.iteration.request.CustomIteration;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public abstract class AccountBookRequest {

    @NotNull(message = AccountBookResponseMessages.DATE_REQUIRED)
    private String date;

    @NotBlank(message = AccountBookResponseMessages.CATEGORY_NAME_REQUIRED)
    private String categoryName;

    @Size(max = AccountBookValidationConstraints.TITLE_MAX_SIZE,
            message = AccountBookResponseMessages.TITLE_TOO_LONG)
    private String title;

    @NotNull(message = AccountBookResponseMessages.COST_REQUIRED)
    @Positive(message = AccountBookResponseMessages.COST_NEGATIVE)
    @Max(value = AccountBookValidationConstraints.COST_LIMIT,
            message = AccountBookResponseMessages.COST_TOO_BIG)
    private Long cost;

    @Size(max = AccountBookValidationConstraints.MEMO_MAX_SIZE,
            message = AccountBookResponseMessages.MEMO_TOO_LONG)
    private String memo;

    private String iterationType;

    private String iterationAction;

    private Boolean isIterationModified;

    @Valid
    private CustomIteration customIteration;

    public LocalDate parseDate() {
        try {
            return LocalDate.parse(date, DateTimeFormatter.ofPattern(DatePattern.LOCAL_DATE_PATTERN));
        } catch (DateTimeException e) {
            throw new BadRequestException(AccountBookResponse.DATE_INVALID);
        }
    }

    public String trimTitle() {
        if (title == null) {
            return null;
        }

        String trimTitle = title.trim();
        if (!trimTitle.isEmpty()) {
            return trimTitle;
        }

        throw new BadRequestException(AccountBookResponse.TITLE_TOO_SHORT);
    }

    public IterationType parseIterationType() {
        return IterationType.from(iterationType);
    }

    public IterationAction parseIterationAction() {
        return IterationAction.from(iterationAction);
    }
}
