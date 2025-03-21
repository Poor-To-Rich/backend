package com.poortorich.user.constants;

public class UserConstant {

    private UserConstant() {
    }

    public static final class Path {

        public static final String REGISTER = "/register";
        public static final String CHECK_USERNAME_DUPLICATE = "/register/username/exists";
    }

    public static final class ValidationMessages {

        public static final String NAME_REQUIRED = "이름을 입력해주세요.";
        public static final String NAME_TOO_LONG = "이름은 10자 이하로 작성해야 합니다.";
        public static final String NAME_CONTAINS_BLANK = "이름은 공백없이 작성해야 합니다.";

        public static final String NICKNAME_REQUIRED = "닉네임을 입력해주세요.";
        public static final String NICKNAME_TOO_LONG = "닉네임은 10자 이하로 작성해야 합니다.";
        public static final String NICKNAME_CONTAINS_BLANK = "닉네임은 공백없이 작성해야 합니다.";
        public static final String NICKNAME_CONTAINS_SPECIAL_CHAR = "닉네임에 특수문자를 사용할 수 없습니다.";
        public static final String NICKNAME_INVALID_START_CHAR = "닉네임은 한글/영문자로 시작해야 합니다.";
        public static final String NICKNAME_DUPLICATE = "이미 사용 중인 닉네임입니다.";

        public static final String USERNAME_REQUIRED = "아이디를 입력해주세요.";
        public static final String USERNAME_CONTAINS_INVALID_CHAR = "아이디는 영문자와 숫자로만 작성해야 합니다.";
        public static final String USERNAME_CONTAINS_BLANK = "아이디는 공백없이 작성해야 합니다.";
        public static final String USERNAME_LENGTH_INVALID = "아이디는 4 ~ 10자리로 작성해야 합니다.";

        public static final String PASSWORD_REQUIRED = "비밀번호를 입력해주세요.";
        public static final String PASSWORD_CONTAINS_KOREAN = "비밀번호에 한글은 사용할 수 없습니다.";
        public static final String PASSWORD_CONTAINS_BLANK = "비밀번호는 공백없이 작성해야 합니다.";
        public static final String PASSWORD_INVALID = "영문자, 특수문자, 숫자가 하나 이상 포함되어야 합니다.";
        public static final String PASSWORD_LENGTH_INVALID = "비밀번호는 8 ~ 15자리로 작성해야 합니다.";
        public static final String PASSWORD_DO_NOT_MATCH = "비밀번호와 비밀번호 재입력이 일치하지 않습니다.";

        public static final String PASSWORD_CONFIRM_REQUIRED = "비밀번호 재입력칸을 입력해주세요.";

        public static final String BIRTHDAY_REQUIRED = "생년월일을 입력해주세요.";
        public static final String BIRTHDAY_INVALID = "yyyy.mm.dd 형식이어야 합니다.";

        public static final String EMAIL_REQUIRED = "이메일을 입력해주세요.";
        public static final String EMAIL_INVALID = "잘못된 이메일 형식입니다.";
        public static final String EMAIL_DUPLICATE = "이미 사용중인 이메일입니다.";

        public static final String GENDER_REQUIRED = "성별을 입력해주세요.";
    }

    public static final class ValidationConstraints {

        public static final int NAME_MAX_LENGTH = 10;

        public static final int NICKNAME_MAX_LENGTH = 10;
        public static final String NICKNAME_START_REGEX = "^[가-힣a-zA-Z].*$";
        public static final String NICKNAME_ALLOWED_CHARS_REGEX = "^[가-힣a-zA-Z0-9]*$";

        public static final int USERNAME_MIN_LENGTH = 4;
        public static final int USERNAME_MAX_LENGTH = 10;
        public static final String USERNAME_ALLOWED_CHARS_REGEX = "^[a-zA-Z0-9]*$";

        public static final int PASSWORD_MIN_LENGTH = 8;
        public static final int PASSWORD_MAX_LENGTH = 15;
        public static final String PASSWORD_REGEX = "^(?=.*[a-zA-Z])(?=.*[0-9])"
                + "(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?]).*$";
        public static final String PASSWORD_NO_KOREAN_REGEX = "^[가-힣]*$";

        public static final String BIRTHDAY_FORMAT_REGEX = "^\\d{4}\\.\\d{2}\\.\\d{2}$";
        public static final String NO_BLANK_REGEX = "^\\S*$";
    }

    public static final class Database {

        public static final String USER_TABLE = "user";
        public static final String ID_COLUMN = "id";
        public static final String USERNAME_COLUMN = "username";
        public static final String PASSWORD_COLUMN = "password";
        public static final String NAME_COLUMN = "name";
        public static final String NICKNAME_COLUMN = "nickname";
        public static final String EMAIL_COLUMN = "email";
        public static final String GENDER_COLUMN = "gender";
        public static final String BIRTHDAY_COLUMN = "birth";
        public static final String PROFILE_IMAGE_COLUMN = "profileImage";
        public static final String JOB_COLUMN = "job";
        public static final String CREATED_DATE_COLUMN = "createdDate";
        public static final String UPDATED_DATE_COLUMN = "updatedDate";
    }
}
