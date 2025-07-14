package com.poortorich.user.constants;

public class UserResponseMessages {

    public static final String PROFILE_IMAGE_REQUIRED = "프로필 이미지를 등록해주세요.";
    public static final String NAME_REQUIRED = "이름을 입력해주세요.";
    public static final String NAME_TOO_LONG = "이름은 10자 이하로 작성해야 합니다.";
    public static final String NAME_CONTAINS_BLANK = "이름은 공백없이 작성해야 합니다.";

    public static final String NICKNAME_REQUIRED = "닉네임을 입력해주세요.";
    public static final String NICKNAME_TOO_LONG = "닉네임은 10자 이하로 작성해야 합니다.";
    public static final String NICKNAME_CONTAINS_BLANK = "닉네임은 공백없이 작성해야 합니다.";
    public static final String NICKNAME_CONTAINS_SPECIAL_CHAR = "닉네임에 특수문자를 사용할 수 없습니다.";
    public static final String NICKNAME_INVALID_START_CHAR = "닉네임은 한글/영문자로 시작해야 합니다.";
    public static final String NICKNAME_DUPLICATE = "이미 사용 중인 닉네임입니다.";
    public static final String NICKNAME_AVAILABLE = "사용 가능한 닉네임입니다.";
    public static final String NICKNAME_RESERVE_CHECK_REQUIRED = "닉네임 중복 검사를 먼저 진행해주세요.";

    public static final String USERNAME_REQUIRED = "아이디를 입력해주세요.";
    public static final String USERNAME_CONTAINS_INVALID_CHAR = "아이디는 영문자와 숫자로만 작성해야 합니다.";
    public static final String USERNAME_CONTAINS_BLANK = "아이디는 공백없이 작성해야 합니다.";
    public static final String USERNAME_LENGTH_INVALID = "아이디는 4 ~ 10자리로 작성해야 합니다.";
    public static final String USERNAME_DUPLICATE = "이미 사용 중인 아이디입니다.";
    public static final String USERNAME_AVAILABLE = "사용 가능한 아이디입니다.";
    public static final String USERNAME_RESERVE_CHECK_REQUIRED = "아이디 중복 검사를 먼저 진행해주세요.";
    public static final String USERNAME_NOT_MATCH = "입력한 회원 정보와 일치하는 회원을 찾을 수 없습니다";

    public static final String PASSWORD_REQUIRED = "비밀번호를 입력해주세요.";
    public static final String PASSWORD_CONTAINS_KOREAN = "비밀번호에 한글은 사용할 수 없습니다.";
    public static final String PASSWORD_CONTAINS_BLANK = "비밀번호는 공백없이 작성해야 합니다.";
    public static final String PASSWORD_INVALID = "비밀번호는 영문 소문자, 숫자, 특수문자를 각각 최소 하나 이상 포함해야 합니다.";
    public static final String PASSWORD_LENGTH_INVALID = "비밀번호는 8 ~ 15자리로 작성해야 합니다.";
    public static final String PASSWORD_DO_NOT_MATCH = "비밀번호와 비밀번호 재입력이 일치하지 않습니다.";
    public static final String PASSWORD_CONFIRM_REQUIRED = "비밀번호 재입력칸을 입력해주세요.";

    public static final String PASSWORD_UPDATE_SUCCESS = "비밀번호 변경 성공";
    public static final String CURRENT_PASSWORD_REQUIRED = "현재 비밀번호를 입력해주세요.";
    public static final String NEW_PASSWORD_REQUIRED = "새 비밀번호를 입력해주세요.";
    public static final String CURRENT_PASSWORD_IS_WRONG = "비밀번호가 틀렸습니다. 다시 입력해주세요.";
    public static final String NEW_PASSWORD_IS_DO_NOT_MATCH = "새 비밀번호가 일치하지 않습니다.";

    public static final String BIRTHDAY_REQUIRED = "생년월일을 입력해주세요.";
    public static final String BIRTHDAY_FORMAT_INVALID = "yyyy-mm-dd 형식이어야 합니다.";
    public static final String BIRTHDAY_IN_FUTURE = "생년월일은 오늘 이전 날짜여야 합니다.";

    public static final String EMAIL_REQUIRED = "이메일을 입력해주세요.";
    public static final String EMAIL_INVALID = "잘못된 이메일 형식입니다.";
    public static final String EMAIL_DUPLICATE = "이미 사용중인 이메일입니다.";

    public static final String GENDER_REQUIRED = "성별을 입력해주세요.";
    public static final String GENDER_INVALID = "잘못된 성별입니다.";

    public static final String REGISTRATION_SUCCESS = "회원 가입 성공";
    public static final String REGISTRATION_REQUEST_REQUIRED = "회원가입을 위해 필요한 정보를 입력해주세요.";

    public static final String USER_DETAIL_FIND_SUCCESS = "회원 상세 조회 성공";
    public static final String USER_NOT_FOUND = "회원 정보를 찾을 수 없습니다.";

    public static final String USER_PROFILE_UPDATE_SUCCESS = "회원 프로필 편집 성공";
    public static final String USER_EMAIL_UPDATE_SUCCESS = "이메일을 변경했습니다";

    public static final String USER_EMAIL_FIND_SUCCESS = "회원 이메일 조회 성공";
    public static final String RESET_SUCCESS = "모든 데이터를 삭제하였습니다.";
    public static final String DELETE_USER_ACCOUNT_SUCCESS = "성공적으로 탈퇴되었습니다.";

    public static final String USER_ROLE_FIND_SUCCESS = "사용자 역할 조회 성공";
    public static final String USER_ROLE_INVALID = "유효하지 않은 역할입니다.";

    public static final String FIND_USERNAME_SUCCESS = "아이디 찾기 성공";

    private UserResponseMessages() {
    }
}
