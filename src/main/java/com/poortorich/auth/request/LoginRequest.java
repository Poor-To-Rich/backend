package com.poortorich.auth.request;

import com.poortorich.auth.constants.AuthResponseMessage;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class LoginRequest {

    @NotBlank(message = AuthResponseMessage.USERNAME_REQUIRED)
    private String username;

    @NotBlank(message = AuthResponseMessage.PASSWORD_REQUIRED)
    private String password;
}
