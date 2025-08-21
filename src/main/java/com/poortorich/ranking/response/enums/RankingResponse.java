package com.poortorich.ranking.response.enums;

import com.poortorich.global.response.Response;
import com.poortorich.ranking.constants.RankingResponseMessage;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum RankingResponse implements Response {

    GET_LATEST_RANKING_SUCCESS(HttpStatus.OK, RankingResponseMessage.GET_LATEST_RANKING_SUCCESS, null),
    GET_LATEST_RANKING_NOT_FOUND(HttpStatus.OK, RankingResponseMessage.GET_LATEST_RANKING_NOT_FOUND, null);

    private final HttpStatus httpStatus;
    private final String message;
    private final String field;

    @Override
    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public String getField() {
        return field;
    }
}
