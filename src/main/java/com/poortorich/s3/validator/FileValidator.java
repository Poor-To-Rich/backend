package com.poortorich.s3.validator;

import com.poortorich.global.exceptions.BadRequestException;
import com.poortorich.s3.constants.S3ValidationConstraints;
import com.poortorich.s3.response.enums.S3Response;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public class FileValidator {

    public void validateFileType(MultipartFile imageFile) {
        if (!S3ValidationConstraints.ALLOWED_FILE_TYPES.contains(imageFile.getContentType())) {
            throw new BadRequestException(S3Response.INVALID_FILE_TYPES, "profileImage");
        }
    }

    public void validateFileSize(MultipartFile imageFile) {
        if (S3ValidationConstraints.FILE_MAX_SIZE < imageFile.getSize()) {
            throw new BadRequestException(S3Response.INVALID_FILE_SIZE, "profileImage");
        }
    }
}
