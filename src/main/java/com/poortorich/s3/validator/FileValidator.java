package com.poortorich.s3.validator;

import com.poortorich.global.exceptions.BadRequestException;
import com.poortorich.s3.constants.S3Constants.ValidationConstraints;
import com.poortorich.s3.response.enums.S3Response;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public class FileValidator {

    public void validatoeFileType(MultipartFile imageFile) {
        if (!ValidationConstraints.ALLOWED_FILE_TYPES.contains(imageFile.getContentType())) {
            throw new BadRequestException(S3Response.INVALID_FILE_TYPES);
        }
    }

    public void validateFileSize(MultipartFile imageFile) {
        if (ValidationConstraints.FILE_MAX_SIZE < imageFile.getSize()) {
            throw new BadRequestException(S3Response.FILE_SIZE_INVALID);
        }
    }
}
