package com.poortorich.email.request;

public class EmailBlockTimeResponse {

    private static final String BLOCK_MESSAGE_FORM = "분 후 다시 시도해주세요.";
    private static final String AVAILABLE_MESSAGE = "사용 가능한 이메일입니다.";

    private final long blockTime;
    private final String message;
    private final boolean isBlocked;

    public EmailBlockTimeResponse(long blockTime) {
        this.blockTime = blockTime;
        this.message = blockTime + BLOCK_MESSAGE_FORM;
        this.isBlocked = true;
    }

    public EmailBlockTimeResponse() {
        this.blockTime = 0L;
        this.message = AVAILABLE_MESSAGE;
        this.isBlocked = false;
    }
}
