package com.poortorich.user.constants;

public class UserValidationRules {
    public static final int NAME_MAX_LENGTH = 10;

    public static final int NICKNAME_MAX_LENGTH = 10;
    public static final String NICKNAME_START_PATTERN = "^[가-힣a-zA-Z].*$";
    public static final String NICKNAME_ALLOWED_CHARS_PATTERN = "^[가-힣a-zA-Z0-9]*$";

    public static final int USERNAME_MIN_LENGTH = 4;
    public static final int USERNAME_MAX_LENGTH = 10;
    public static final String USERNAME_ALLOWED_CHARS_PATTERN = "^[a-zA-Z0-9]*$";

    public static final int PASSWORD_MIN_LENGTH = 8;
    public static final int PASSWORD_MAX_LENGTH = 15;
    public static final String PASSWORD_PATTERN = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[\\W_]).+$";
    public static final String PASSWORD_NO_KOREAN_PATTERN = "^[가-힣]*$";

    public static final String BIRTHDAY_DATE_FORMAT = "yyyy.MM.dd";
    public static final String BIRTHDAY_FORMAT_PATTERN = "^\\d{4}\\.\\d{2}\\.\\d{2}$";
    public static final String NO_BLANK_PATTERN = "^\\S*$";
}
