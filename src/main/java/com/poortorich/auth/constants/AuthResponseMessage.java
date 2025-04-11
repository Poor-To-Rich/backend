package com.poortorich.auth.constants;

public class AuthResponseMessage {

    public static final String USERNAME_REQUIRED = "아이디를 입력해주세요.";
    public static final String PASSWORD_REQUIRED = "비밀번호를 입력해주세요.";
    public static final String CREDENTIALS_INVALID = "아이디 또는 비밀번호가 틀렸습니다. 다시 입력해주세요.";

    public static final String TOKEN_INVALID = "인증 정보가 유효하지 않습니다. 다시 로그인해주세요.";
    public static final String ACCESS_TOKEN_EXPIRED = "액세스 토큰이 만료되었습니다. 갱신해주세요.";
    public static final String TOKEN_REFRESH_SUCCESS = "토큰 갱신 성공";

    public static final String LOGIN_SUCCESS = "로그인 성공";
    public static final String LOGOUT_SUCCESS = "로그아웃 성공";

    public static final String REDIS_SERVER_EXCEPTION = "요청을 처리하던 중 서버에 오류가 발생했습니다. 잠시 후 다시 시도해주세요.";

    private AuthResponseMessage() {
    }
}
