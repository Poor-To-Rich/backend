package com.poortorich.s3.service;

import com.amazonaws.AmazonClientException;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.poortorich.global.exceptions.InternalServerErrorException;
import com.poortorich.s3.constants.S3TestConfig;
import com.poortorich.s3.constants.S3TestExceptionMessages;
import com.poortorich.s3.response.enums.S3Response;
import com.poortorich.s3.util.S3TestFileGenerator;
import com.poortorich.s3.validator.FileValidator;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

@ExtendWith(MockitoExtension.class)
class FileUploadServiceTest {

    private static final String BUCKET_VARIABLE_NAME = "bucketName";
    private static final String EXPECTED_FILE_NAME = "test-file.jpg";

    @Mock
    private AmazonS3Client amazonS3Client;

    @Mock
    private FileValidator fileValidator;

    @Mock
    private MockMultipartFile testFileWithIOException;

    @InjectMocks
    private FileUploadService fileUploadService;

    private MockMultipartFile testFile;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(fileUploadService, BUCKET_VARIABLE_NAME, S3TestConfig.BUCKET_NAME);

        testFile = S3TestFileGenerator.createJpegFile();
    }

    @Test
    void uploadImage_ShouldUploadAndReturnUrl() throws IOException {

        URL mockUrl = URI.create(S3TestConfig.FILE_URL).toURL();

        Mockito.doNothing().when(fileValidator).validateFileType(Mockito.any(MultipartFile.class));
        Mockito.doNothing().when(fileValidator).validateFileSize(Mockito.any(MultipartFile.class));
        Mockito.when(amazonS3Client.getUrl(Mockito.anyString(), Mockito.anyString())).thenReturn(mockUrl);

        String resultUrl = fileUploadService.uploadImage(testFile);

        Assertions.assertEquals(S3TestConfig.FILE_URL, resultUrl);
        Mockito.verify(fileValidator).validateFileType(testFile);
        Mockito.verify(fileValidator).validateFileSize(testFile);
        Mockito.verify(amazonS3Client).putObject(Mockito.any(PutObjectRequest.class));
        Mockito.verify(amazonS3Client).getUrl(Mockito.eq(S3TestConfig.BUCKET_NAME), Mockito.anyString());
    }

    @Test
    void uploadImage_WhenGetInputStreamThrowsIOException_ShouldThrowInternalServerErrorException() throws IOException {
        Mockito.doNothing().when(fileValidator).validateFileType(Mockito.any(MultipartFile.class));
        Mockito.doNothing().when(fileValidator).validateFileSize(Mockito.any(MultipartFile.class));
        Mockito.when(testFileWithIOException.getOriginalFilename()).thenReturn("test.jpg");
        Mockito.when(testFileWithIOException.getInputStream())
                .thenThrow(new IOException(S3TestExceptionMessages.GLOBAL_EXCEPTION_MESSAGE));

        InternalServerErrorException exception = Assertions.assertThrows(
                InternalServerErrorException.class,
                () -> fileUploadService.uploadImage(testFileWithIOException)
        );

        Assertions.assertEquals(S3Response.FILE_UPLOAD_FAILURE, exception.getResponse());
    }

    @Test
    void uploadImage_WhenPutObjectThrowsAmazonClientException_ShouldThrowInternalServerErrorException() {
        Mockito.doNothing().when(fileValidator).validateFileType(Mockito.any(MultipartFile.class));
        Mockito.doNothing().when(fileValidator).validateFileSize(Mockito.any(MultipartFile.class));
        Mockito.doThrow(new AmazonClientException(S3TestExceptionMessages.GLOBAL_EXCEPTION_MESSAGE))
                .when(amazonS3Client)
                .putObject(Mockito.any(PutObjectRequest.class));

        InternalServerErrorException exception = Assertions.assertThrows(
                InternalServerErrorException.class,
                () -> fileUploadService.uploadImage(testFile)
        );

        Assertions.assertEquals(S3Response.FILE_UPLOAD_FAILURE, exception.getResponse());
    }

    @Test
    void deleteImage_ShouldExtractFileNameAndDeleteObject() {
        fileUploadService.deleteImage(S3TestConfig.FILE_URL);

        ArgumentCaptor<String> fileNameCaptor = ArgumentCaptor.forClass(String.class);
        Mockito.verify(amazonS3Client).deleteObject(Mockito.eq(S3TestConfig.BUCKET_NAME), fileNameCaptor.capture());

        String capturedFileName = fileNameCaptor.getValue();
        Assertions.assertEquals(EXPECTED_FILE_NAME, capturedFileName);
    }
}
