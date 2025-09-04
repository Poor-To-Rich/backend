package com.poortorich.user.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.poortorich.global.exceptions.NotFoundException;
import com.poortorich.s3.constants.S3TestFile;
import com.poortorich.user.constants.UserResponseMessages;
import com.poortorich.user.entity.User;
import com.poortorich.user.entity.enums.Gender;
import com.poortorich.user.fixture.UserFixture;
import com.poortorich.user.repository.UserRepository;
import com.poortorich.user.request.ProfileUpdateRequest;
import com.poortorich.user.request.UserRegistrationRequest;
import com.poortorich.user.response.UserDetailResponse;
import com.poortorich.user.response.UserEmailResponse;
import com.poortorich.user.response.enums.UserResponse;
import com.poortorich.user.util.ProfileUpdateRequestTestBuilder;
import com.poortorich.user.util.UserRegistrationRequestTestBuilder;

import java.util.List;
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
    @DisplayName("사용자 정보를 갱신한다.")
    void update_shouldUpdateUser() {
        User user = UserFixture.createDefaultUser();
        ProfileUpdateRequest profileUpdateRequest = ProfileUpdateRequestTestBuilder.builder()
                .name(UserFixture.VALID_NAME_SAMPLE_2)
                .nickname(UserFixture.VALID_NICKNAME_SAMPLE_2)
                .birth(UserFixture.VALID_BIRTH_SAMPLE_2)
                .gender(UserFixture.VALID_FEMALE)
                .build();

        String newProfileImage = S3TestFile.FILE_NAME;

        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user));

        userService.update(user.getUsername(), profileUpdateRequest, newProfileImage);

        assertThat(user.getProfileImage()).isEqualTo(newProfileImage);
        assertThat(user.getName()).isEqualTo(UserFixture.VALID_NAME_SAMPLE_2);
        assertThat(user.getNickname()).isEqualTo(UserFixture.VALID_NICKNAME_SAMPLE_2);
        assertThat(user.getGender()).isEqualTo(Gender.from(UserFixture.VALID_FEMALE));
        assertThat(user.getBirth().toString()).isEqualTo(UserFixture.VALID_BIRTH_SAMPLE_2);
    }

    @Test
    @DisplayName("회원 프로필 편집 호출했을 때, 회원을 찾을 수 없다면 예외를 발생시킨다.")
    void update_userIsNotExists_shouldThrowException() {
        when(userRepository.findByUsername(anyString())).thenThrow(new NotFoundException(UserResponse.USER_NOT_FOUND));

        assertThatThrownBy(
                () -> userService.update(
                        UserFixture.VALID_USERNAME_SAMPLE_1,
                        ProfileUpdateRequestTestBuilder.builder().build(),
                        S3TestFile.FILE_NAME))
                .isInstanceOf(NotFoundException.class)
                .hasMessage(UserResponseMessages.USER_NOT_FOUND);
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

    @Test
    @DisplayName("회원이 존재할 때 이메일을 반환한다.")
    void getUserEmail_whenUserExists_thenReturnEmail() {
        User mockUser = UserFixture.createDefaultUser();

        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(mockUser));

        UserEmailResponse userEmail = userService.getUserEmail(UserFixture.VALID_USERNAME_SAMPLE_1);

        assertThat(userEmail).isNotNull();
        assertThat(userEmail.getEmail()).isEqualTo(mockUser.getEmail());

        verify(userRepository, times(1)).findByUsername(anyString());
    }

    @Test
    @DisplayName("회원이 존재할 때 비밀번호를 변경한다.")
    void updateUserPassword_whenUserExists_thenUpdatePassword() {
        User mockUser = UserFixture.createDefaultUser();
        String newPassword = UserFixture.VALID_PASSWORD_SAMPLE_2;
        String encodedNewPassword = UserFixture.TEST_ENCODED_PASSWORD;

        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(mockUser));
        when(passwordEncoder.encode(anyString())).thenReturn(encodedNewPassword);

        userService.updatePassword(mockUser.getUsername(), newPassword);

        assertThat(mockUser.getPassword()).isEqualTo(encodedNewPassword);

        verify(userRepository, times(1)).findByUsername(anyString());
    }

    @Test
    @DisplayName("유저 아이디 목록으로 유저 조회 성공")
    void findAllByIdInSuccess() {
        List<Long> ids = List.of(1L, 2L, 3L);
        User user1 = User.builder().id(1L).build();
        User user2 = User.builder().id(2L).build();
        User user3 = User.builder().id(3L).build();

        when(userRepository.findAllByIdIn(ids))
                .thenReturn(List.of(user1, user2, user3));

        List<User> result = userService.findAllByIdIn(ids);

        assertThat(result).hasSize(3);
        assertThat(result).containsExactly(user1, user2, user3);
    }
}
