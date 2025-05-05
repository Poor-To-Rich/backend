package com.poortorich.email.enums;

import com.poortorich.email.response.enums.EmailResponse;
import com.poortorich.global.exceptions.BadRequestException;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum EmailTemplateType {

    REGISTRATION("register", "부자될거지(Poor To Rich) 회원 가입 인증 코드 안내", "회원 가입"),

    EMAIL_CHANGE("emailChange", "부자될거지(Poor To Rich) 이메일 변경 인증 코드 안내", "이메일 변경");

    public static final String BODY_TEMPLATE = """
            안녕하세요, 고객님
            부자될거지(Poor To Rich) %s 인증 코드 안내 메일입니다.
            아래의 인증 코드를 입력하여 주세요.
            
            인증 코드: %s
            
            개인 정보 보호를 위해 인증 코드는 10분 간 유효합니다.
            감사합니다.
            """;

    private final String purpose;

    private final String subject;

    private final String description;

    public static EmailTemplateType getTemplateByPurpose(String purpose) {
        for (var templateType : EmailTemplateType.values()) {
            if (templateType.getPurpose().equals(purpose)) {
                return templateType;
            }
        }
        throw new BadRequestException(EmailResponse.PURPOSE_INVALID);
    }
}
