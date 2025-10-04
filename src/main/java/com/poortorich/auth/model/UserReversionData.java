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

    private String name;
    private String nickname;
    private String email;
    private Gender gender;
    private Role role;
    private String profileImage;
    private String identify;
    private String username;
}
