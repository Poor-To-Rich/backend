package com.poortorich.auth.model;

import com.poortorich.user.entity.enums.Gender;
import com.poortorich.user.entity.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserReversionData {

    private String name = null;
    private String nickname = null;
    private String email = null;
    private Gender gender = null;
    private Role role = null;
    private String profileImage = null;
    private String identify = null;
    private String username = null;
    private String password = null;
}
