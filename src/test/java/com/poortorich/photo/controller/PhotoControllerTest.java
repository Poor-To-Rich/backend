package com.poortorich.photo.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.poortorich.global.config.BaseSecurityTest;
import com.poortorich.photo.facade.PhotoFacade;
import com.poortorich.photo.request.PhotoUploadRequest;
import com.poortorich.photo.response.PhotoUploadResponse;
import com.poortorich.photo.response.PreviewPhotosResponse;
import com.poortorich.photo.response.enums.PhotoResponse;
import com.poortorich.s3.util.S3TestFileGenerator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PhotoController.class)
@ExtendWith(MockitoExtension.class)
class PhotoControllerTest extends BaseSecurityTest {

    private static final String USERNAME = "test";

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private PhotoFacade photoFacade;

    @Test
    @WithMockUser(username = USERNAME)
    @DisplayName("채팅방 이미지 업로드 성공")
    void uploadPhotoSuccess() throws Exception {
        Long chatroomId = 1L;
        MockMultipartFile photo = S3TestFileGenerator.createJpegFile();
        when(photoFacade.uploadPhoto(eq(USERNAME), eq(chatroomId), any(PhotoUploadRequest.class)))
                .thenReturn(PhotoUploadResponse.builder().build());

        mockMvc.perform(multipart("/chatrooms/" + chatroomId + "/photos")
                        .file(photo)
                        .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
                        .with(csrf()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message")
                        .value(PhotoResponse.UPLOAD_PHOTO_SUCCESS.getMessage()));
    }

    @Test
    @WithMockUser(username = USERNAME)
    @DisplayName("최신 사진 목록 조회 성공")
    void getPreviewPhotosSuccess() throws Exception {
        Long chatroomId = 1L;

        when(photoFacade.getPreviewPhotos(eq(USERNAME), eq(chatroomId)))
                .thenReturn(PreviewPhotosResponse.builder().build());

        mockMvc.perform(get("/chatrooms/" + chatroomId + "/photos/preview")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message")
                        .value(PhotoResponse.GET_PREVIEW_PHOTOS_SUCCESS.getMessage()));
    }
}
