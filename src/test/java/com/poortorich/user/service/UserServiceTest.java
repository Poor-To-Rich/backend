package com.poortorich.user.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.poortorich.user.entity.User;
import com.poortorich.user.fixture.UserRegistrationFixture;
import com.poortorich.user.repository.UserRepository;
import com.poortorich.user.request.UserRegistrationRequest;
import com.poortorich.user.util.UserRegistrationRequestTestBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    private final String profileImageUrl = UserRegistrationFixture.TEST_PROFILE_IMAGE_URL;
    private final String encodedPassword = UserRegistrationFixture.TEST_ENCODED_PASSWORD;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @Captor
    private ArgumentCaptor<User> userCaptor;

    private UserRegistrationRequest userRegistrationRequest;

    @BeforeEach
    void setUp() {
        userRegistrationRequest = new UserRegistrationRequestTestBuilder().build();
        when(passwordEncoder.encode(userRegistrationRequest.getPassword())).thenReturn(encodedPassword);
    }

    @Test
    @DisplayName("사용자 정보를 저장하고 User를 생성한다.")
    void save_shouldCreateAndSaveUser() {
        userService.save(userRegistrationRequest, profileImageUrl);

        verify(userRepository).save(userCaptor.capture());
        User savedUser = userCaptor.getValue();

        assertThat(savedUser.getProfileImage()).isEqualTo(profileImageUrl);
        assertThat(savedUser.getUsername()).isEqualTo(userRegistrationRequest.getUsername());
        assertThat(savedUser.getPassword()).isEqualTo(encodedPassword);
        assertThat(savedUser.getName()).isEqualTo(userRegistrationRequest.getName());
        assertThat(savedUser.getNickname()).isEqualTo(userRegistrationRequest.getNickname());
        assertThat(savedUser.getEmail()).isEqualTo(userRegistrationRequest.getEmail());
        assertThat(savedUser.getGender()).isEqualTo(userRegistrationRequest.getGender());
        assertThat(savedUser.getBirth()).isEqualTo(userRegistrationRequest.getBirthday());
        assertThat(savedUser.getJob()).isEqualTo(userRegistrationRequest.getJob());
    }

    @Test
    @DisplayName("사용자 비밀번호를 인코딩한다.")
    void save_shouldEncodedPassword() {
        userService.save(userRegistrationRequest, profileImageUrl);

        verify(passwordEncoder).encode(userRegistrationRequest.getPassword());
    }
}
