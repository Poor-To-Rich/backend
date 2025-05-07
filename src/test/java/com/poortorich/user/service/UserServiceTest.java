package com.poortorich.user.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.poortorich.user.entity.User;
import com.poortorich.user.fixture.UserFixture;
import com.poortorich.user.repository.UserRepository;
import com.poortorich.user.request.UserRegistrationRequest;
import com.poortorich.user.response.UserDetailResponse;
import com.poortorich.user.util.UserRegistrationRequestTestBuilder;
import java.util.Optional;
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

    private final String profileImageUrl = UserFixture.TEST_PROFILE_IMAGE_URL;
    private final String encodedPassword = UserFixture.TEST_ENCODED_PASSWORD;

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
        lenient().when(passwordEncoder.encode(userRegistrationRequest.getPassword())).thenReturn(encodedPassword);
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
        assertThat(savedUser.getGender()).isEqualTo(userRegistrationRequest.parseGender());
        assertThat(savedUser.getBirth()).isEqualTo(userRegistrationRequest.parseBirthday());
        assertThat(savedUser.getJob()).isEqualTo(userRegistrationRequest.getJob());
    }

    @Test
    @DisplayName("사용자 비밀번호를 인코딩한다.")
    void save_shouldEncodedPassword() {
        userService.save(userRegistrationRequest, profileImageUrl);

        verify(passwordEncoder).encode(userRegistrationRequest.getPassword());
    }

    @Test
    @DisplayName("회원이 존재할 때 User를 반환한다.")
    void findByUsername_userExists_returnUser() {
        User mockUser = UserFixture.createDefaultUser();

        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(mockUser));

        UserDetailResponse userDetails = userService.findUserDetailByUsername(anyString());

        assertThat(userDetails).isNotNull();
        assertThat(userDetails.getProfileImage()).isEqualTo(mockUser.getProfileImage());
        assertThat(userDetails.getName()).isEqualTo(mockUser.getName());
        assertThat(userDetails.getNickname()).isEqualTo(mockUser.getNickname());
        assertThat(userDetails.getBirth()).isEqualTo(mockUser.getBirth().toString());
        assertThat(userDetails.getGender()).isEqualTo(mockUser.getGender().toString());
        assertThat(userDetails.getJob()).isEqualTo(mockUser.getJob());

        verify(userRepository, times(1)).findByUsername(anyString());
    }
}
