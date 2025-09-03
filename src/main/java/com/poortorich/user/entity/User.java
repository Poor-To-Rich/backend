package com.poortorich.user.entity;

import com.poortorich.s3.constants.S3Constants;
import com.poortorich.user.constants.UserValidationRules;
import com.poortorich.user.entity.enums.Gender;
import com.poortorich.user.entity.enums.Role;
import com.poortorich.user.request.ProfileUpdateRequest;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "user")
@DynamicUpdate
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "username", nullable = false, unique = true)
    private String username;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "nickname", nullable = false, unique = true)
    private String nickname;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "gender", nullable = false)
    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Column(name = "birth", nullable = false)
    private LocalDate birth;

    @Column(name = "profile_image", nullable = false)
    private String profileImage;

    @Column(name = "job")
    private String job;

    @Column(name = "role", nullable = false)
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private Role role = Role.USER;

    @Column(name = "withdraw_at")
    private LocalDateTime withdrawAt;

    @Column(name = "created_date", nullable = false)
    @CreationTimestamp
    private LocalDateTime createdDate;

    @Column(name = "updated_date", nullable = false)
    @UpdateTimestamp
    private LocalDateTime updatedDate;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.getValue()));
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return role != Role.WITHDRAW;
    }

    public void updateProfile(ProfileUpdateRequest userProfile, String newProfileImage) {
        this.profileImage = newProfileImage;
        this.name = userProfile.getName();
        this.nickname = userProfile.getNickname();
        this.gender = Gender.from(userProfile.getGender());
        this.birth = LocalDate.parse(
                userProfile.getBirth(),
                DateTimeFormatter.ofPattern(UserValidationRules.BIRTHDAY_DATE_FORMAT));
        this.job = userProfile.getJob();
    }

    public void updatePassword(String password) {
        this.password = password;
    }

    public void updateEmail(String newEmail) {
        this.email = newEmail;
    }

    public void updateRole(Role role) {
        this.role = role;
    }

    public User updateOAuth(String name, String profileImage) {
        this.profileImage = profileImage;
        this.nickname = name;
        return this;
    }

    public void withdraw(String password) {
        String uuid = UUID.randomUUID().toString();
        this.nickname = uuid;
        this.email = uuid;
        this.username = uuid;
        this.password = password;
        this.role = Role.WITHDRAW;
        this.job = null;
        this.profileImage = S3Constants.DEFAULT_PROFILE_IMAGE;
        this.withdrawAt = LocalDateTime.now();
    }
}
