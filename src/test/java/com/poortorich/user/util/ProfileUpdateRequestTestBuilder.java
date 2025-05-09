package com.poortorich.user.util;

import com.poortorich.s3.util.S3TestFileGenerator;
import com.poortorich.user.fixture.UserFixture;
import com.poortorich.user.request.ProfileUpdateRequest;
import org.springframework.web.multipart.MultipartFile;

public class ProfileUpdateRequestTestBuilder {

    private MultipartFile profileImage = S3TestFileGenerator.createJpegFile();
    private Boolean isDefaultProfile = Boolean.FALSE;
    private String name = UserFixture.VALID_NAME_SAMPLE_1;
    private String nickname = UserFixture.VALID_NICKNAME_SAMPLE_1;
    private String birth = UserFixture.VALID_BIRTH_SAMPLE_1;
    private String gender = UserFixture.VALID_MALE;
    private String job = UserFixture.VALID_JOB_SAMPLE_1;

    public static ProfileUpdateRequestTestBuilder builder() {
        return new ProfileUpdateRequestTestBuilder();
    }

    public ProfileUpdateRequestTestBuilder profileImage(MultipartFile profileImage) {
        this.profileImage = profileImage;
        return this;
    }

    public ProfileUpdateRequestTestBuilder isDefaultProfile(Boolean isDefaultProfile) {
        this.isDefaultProfile = isDefaultProfile;
        return this;
    }

    public ProfileUpdateRequestTestBuilder name(String name) {
        this.name = name;
        return this;
    }

    public ProfileUpdateRequestTestBuilder nickname(String nickname) {
        this.nickname = nickname;
        return this;
    }

    public ProfileUpdateRequestTestBuilder birth(String birth) {
        this.birth = birth;
        return this;
    }

    public ProfileUpdateRequestTestBuilder gender(String gender) {
        this.gender = gender;
        return this;
    }

    public ProfileUpdateRequestTestBuilder job(String job) {
        this.job = job;
        return this;
    }

    public ProfileUpdateRequest build() {
        return new ProfileUpdateRequest(
                profileImage,
                isDefaultProfile,
                name,
                nickname,
                birth,
                gender,
                job
        );
    }
}
