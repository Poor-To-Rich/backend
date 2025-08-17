package com.poortorich.photo.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@AllArgsConstructor
public class PhotoUploadRequest {

    private MultipartFile photo;
}
