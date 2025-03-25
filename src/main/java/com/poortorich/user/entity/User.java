package com.poortorich.user.entity;

import com.poortorich.user.constants.UserDatabase;
import com.poortorich.user.entity.enums.Gender;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Entity
@Table(name = UserDatabase.USER_TABLE)
@DynamicUpdate
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = UserDatabase.ID_COLUMN)
    private Long id;

    @Column(name = UserDatabase.USERNAME_COLUMN, nullable = false, unique = true)
    private String username;

    @Column(name = UserDatabase.PASSWORD_COLUMN, nullable = false)
    private String password;

    @Column(name = UserDatabase.NAME_COLUMN, nullable = false)
    private String name;

    @Column(name = UserDatabase.NICKNAME_COLUMN, nullable = false, unique = true)
    private String nickname;

    @Column(name = UserDatabase.EMAIL_COLUMN, nullable = false, unique = true)
    private String email;

    @Column(name = UserDatabase.GENDER_COLUMN, nullable = false)
    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Column(name = UserDatabase.BIRTHDAY_COLUMN, nullable = false)
    private LocalDate birth;

    @Column(name = UserDatabase.PROFILE_IMAGE_COLUMN, nullable = false)
    private String profileImage;

    @Column(name = UserDatabase.JOB_COLUMN)
    private String job;

    @Column(name = UserDatabase.CREATED_DATE_COLUMN)
    private LocalDateTime createdDate;

    @Column(name = UserDatabase.UPDATED_DATE_COLUMN)
    private LocalDateTime updatedDate;

    @PrePersist
    public void prePersist() {
        this.createdDate = LocalDateTime.now();
        this.updatedDate = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdated() {
        this.updatedDate = LocalDateTime.now();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
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
        return true;
    }
}