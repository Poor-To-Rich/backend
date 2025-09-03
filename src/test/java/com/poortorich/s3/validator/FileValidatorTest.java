package com.poortorich.s3.validator;

import com.poortorich.global.exceptions.BadRequestException;
import com.poortorich.s3.response.enums.S3Response;
import com.poortorich.s3.util.S3TestFileGenerator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.multipart.MultipartFile;

class FileValidatorTest {

    private FileValidator fileValidator;

    @BeforeEach
    void setUp() {
        fileValidator = new FileValidator();
    }

    @Test
    void validateFileType_WithoutValidType_ShouldNotThrowException() {
        MultipartFile jpegFile = S3TestFileGenerator.createJpegFile();
        MultipartFile pngFile = S3TestFileGenerator.createPngFile();

        Assertions.assertDoesNotThrow(() -> fileValidator.validateFileType(jpegFile));
        Assertions.assertDoesNotThrow(() -> fileValidator.validateFileType(pngFile));
    }

    @Test
    void validateFileType_WithInvalidType_ShouldThrowBadRequestException() {
        MultipartFile pdfFile = S3TestFileGenerator.createPdfFile();

        BadRequestException exception = Assertions.assertThrows(
                BadRequestException.class,
                () -> fileValidator.validateFileType(pdfFile)
        );

        Assertions.assertEquals(S3Response.INVALID_FILE_TYPES, exception.getResponse());
    }

    @Test
    void validateFileSize_WithValidSize_ShouldNotThrowException() {
        MultipartFile smallFile = S3TestFileGenerator.createJpegFile();

        Assertions.assertDoesNotThrow(() -> fileValidator.validateFileSize(smallFile));
    }

    @Test
    void validateFileSize_WithInvalidSize_ShouldThrowBadRequestException() {
        MultipartFile largeFile = S3TestFileGenerator.createLargeFile();

        BadRequestException exception = Assertions.assertThrows(
                BadRequestException.class,
                () -> fileValidator.validateFileSize(largeFile)
        );

        Assertions.assertEquals(S3Response.FILE_SIZE_TOO_LARGE, exception.getResponse());
    }
}
