package com.poortorich.photo.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.poortorich.global.config.BaseSecurityTest;
import com.poortorich.photo.facade.PhotoFacade;
import com.poortorich.photo.request.PhotoUploadRequest;
import com.poortorich.photo.response.PhotoUploadResponse;
import com.poortorich.photo.response.enums.PhotoResponse;
import com.poortorich.s3.util.S3TestFileGenerator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.multipart.MultipartFile;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PhotoController.class)
@ExtendWith(MockitoExtension.class)
class PhotoControllerTest extends BaseSecurityTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private PhotoFacade photoFacade;

    private final String username = "test";

    @Test
    @WithMockUser(username = username)
    @DisplayName("채팅방 이미지 업로드 성공")
    void uploadPhotoSuccess() throws Exception {
        Long chatroomId = 1L;
        MockMultipartFile photo = S3TestFileGenerator.createJpegFile();
        when(photoFacade.uploadPhoto(eq(username), eq(chatroomId), any(PhotoUploadRequest.class)))
                .thenReturn(PhotoUploadResponse.builder().build());

        mockMvc.perform(multipart("/chatrooms/" + chatroomId + "/photos")
                        .file(photo)
                        .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
                        .with(csrf()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message")
                        .value(PhotoResponse.UPLOAD_PHOTO_SUCCESS.getMessage()));
    }
}