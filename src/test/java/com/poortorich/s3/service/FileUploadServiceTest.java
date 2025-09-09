package com.poortorich.s3.service;

import com.amazonaws.AmazonClientException;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.poortorich.global.exceptions.InternalServerErrorException;
import com.poortorich.s3.constants.S3Constants;
import com.poortorich.s3.constants.S3TestConfig;
import com.poortorich.s3.constants.S3TestExceptionMessages;
import com.poortorich.s3.response.enums.S3Response;
import com.poortorich.s3.util.S3TestFileGenerator;
import com.poortorich.s3.util.WebpConverter;
import com.poortorich.s3.validator.FileValidator;
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

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
    @Mock
    private WebpConverter webpConverter;

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

        URL mockUrl = URI.create(S3TestConfig.FILE_URL_SAMPLE_1).toURL();

        Mockito.doNothing().when(fileValidator).validateFileType(any(MultipartFile.class));
        Mockito.doNothing().when(fileValidator).validateFileSize(any(MultipartFile.class));
        when(webpConverter.convertToWebp(any(MultipartFile.class)))
                .thenReturn(new byte[]{1, 2, 3});
        when(amazonS3Client.getUrl(Mockito.anyString(), Mockito.anyString())).thenReturn(mockUrl);

        String resultUrl = fileUploadService.uploadImage(testFile);

        assertEquals(S3TestConfig.FILE_URL_SAMPLE_1, resultUrl);
        verify(fileValidator).validateFileType(testFile);
        verify(fileValidator).validateFileSize(testFile);
        verify(amazonS3Client).putObject(any(PutObjectRequest.class));
        verify(amazonS3Client).getUrl(Mockito.eq(S3TestConfig.BUCKET_NAME), Mockito.anyString());
    }

    @Test
    void uploadImage_WhenGetInputStreamThrowsIOException_ShouldThrowInternalServerErrorException() throws IOException {
        Mockito.doNothing().when(fileValidator).validateFileType(any(MultipartFile.class));
        Mockito.doNothing().when(fileValidator).validateFileSize(any(MultipartFile.class));
        when(webpConverter.convertToWebp(any(MultipartFile.class)))
                .thenReturn(new byte[]{1, 2, 3});
        when(testFileWithIOException.getOriginalFilename()).thenReturn("test.jpg");
        doThrow(new AmazonClientException("S3 failure"))
                .when(amazonS3Client)
                .putObject(any(PutObjectRequest.class));
        InternalServerErrorException exception = Assertions.assertThrows(
                InternalServerErrorException.class,
                () -> fileUploadService.uploadImage(testFileWithIOException)
        );

        assertEquals(S3Response.FILE_UPLOAD_FAILURE, exception.getResponse());
    }

    @Test
    void uploadImage_WhenPutObjectThrowsAmazonClientException_ShouldThrowInternalServerErrorException() {
        Mockito.doNothing().when(fileValidator).validateFileType(any(MultipartFile.class));
        Mockito.doNothing().when(fileValidator).validateFileSize(any(MultipartFile.class));
        doThrow(new AmazonClientException(S3TestExceptionMessages.GLOBAL_EXCEPTION_MESSAGE))
                .when(amazonS3Client)
                .putObject(any(PutObjectRequest.class));
        when(webpConverter.convertToWebp(any(MultipartFile.class)))
                .thenReturn(new byte[]{1, 2, 3});

        InternalServerErrorException exception = Assertions.assertThrows(
                InternalServerErrorException.class,
                () -> fileUploadService.uploadImage(testFile)
        );

        assertEquals(S3Response.FILE_UPLOAD_FAILURE, exception.getResponse());
    }

    @Test
    void updateImage_whenIsDefaultProfile_thenDeleteCurrentImageAndReturnDefaultImage() {
        String currentProfileImage = S3TestConfig.FILE_URL_SAMPLE_1;
        MockMultipartFile newProfileImage = S3TestFileGenerator.createEmpty();

        String result = fileUploadService.updateImage(currentProfileImage, newProfileImage, Boolean.TRUE);

        assertEquals(S3Constants.DEFAULT_PROFILE_IMAGE, result);
        ArgumentCaptor<String> fileNameCaptor = ArgumentCaptor.forClass(String.class);
        verify(amazonS3Client).deleteObject(eq(S3TestConfig.BUCKET_NAME), fileNameCaptor.capture());
        assertEquals(EXPECTED_FILE_NAME, fileNameCaptor.getValue());

        verify(amazonS3Client, never()).getUrl(anyString(), anyString());
    }

    @Test
    void updateImage_whenValidNewImage_ShouldDeleteCurrentAndUploadNew() throws MalformedURLException {
        String currentProfileImage = S3TestConfig.FILE_URL_SAMPLE_1;
        MockMultipartFile newProfileImage = S3TestFileGenerator.createJpegFile();

        URL mockUrl = URI.create(S3TestConfig.FILE_URL_SAMPLE_2).toURL();

        Mockito.doNothing().when(fileValidator).validateFileType(any(MultipartFile.class));
        Mockito.doNothing().when(fileValidator).validateFileSize(any(MultipartFile.class));
        when(amazonS3Client.getUrl(Mockito.anyString(), Mockito.anyString())).thenReturn(mockUrl);
        when(webpConverter.convertToWebp(any(MultipartFile.class)))
                .thenReturn(new byte[]{1, 2, 3});
        String result = fileUploadService.updateImage(currentProfileImage, newProfileImage, Boolean.FALSE);

        assertEquals(S3TestConfig.FILE_URL_SAMPLE_2, result);
        ArgumentCaptor<String> fileNameCaptor = ArgumentCaptor.forClass(String.class);
        Mockito.verify(amazonS3Client).deleteObject(Mockito.eq(S3TestConfig.BUCKET_NAME), fileNameCaptor.capture());
        Assertions.assertEquals(EXPECTED_FILE_NAME, fileNameCaptor.getValue());

        Mockito.verify(fileValidator).validateFileType(newProfileImage);
        Mockito.verify(fileValidator).validateFileSize(newProfileImage);
        Mockito.verify(amazonS3Client).putObject(any(PutObjectRequest.class));
        Mockito.verify(amazonS3Client).getUrl(Mockito.eq(S3TestConfig.BUCKET_NAME), Mockito.anyString());
    }

    @Test
    void updateImage_WhenCurrentImageIsDefault_ShouldNotDeleteAndUploadNew() throws MalformedURLException {
        MockMultipartFile newProfileFile = S3TestFileGenerator.createJpegFile();
        URL mockUrl = URI.create(S3TestConfig.FILE_URL_SAMPLE_2).toURL();

        doNothing().when(fileValidator).validateFileType(any(MultipartFile.class));
        doNothing().when(fileValidator).validateFileSize(any(MultipartFile.class));
        when(amazonS3Client.getUrl(anyString(), anyString())).thenReturn(mockUrl);
        when(webpConverter.convertToWebp(any(MultipartFile.class)))
                .thenReturn(new byte[]{1, 2, 3});
        String result = fileUploadService.updateImage(S3Constants.DEFAULT_PROFILE_IMAGE, newProfileFile, Boolean.FALSE);

        assertEquals(S3TestConfig.FILE_URL_SAMPLE_2, result);

        verify(amazonS3Client, never()).deleteObject(anyString(), anyString());
        verify(fileValidator).validateFileType(newProfileFile);
        verify(fileValidator).validateFileSize(newProfileFile);
        verify(amazonS3Client).putObject(any(PutObjectRequest.class));
        verify(amazonS3Client).getUrl(eq(S3TestConfig.BUCKET_NAME), anyString());
    }

    @Test
    void deleteImage_ShouldExtractFileNameAndDeleteObject() {
        fileUploadService.deleteImage(S3TestConfig.FILE_URL_SAMPLE_1);

        ArgumentCaptor<String> fileNameCaptor = ArgumentCaptor.forClass(String.class);
        verify(amazonS3Client).deleteObject(Mockito.eq(S3TestConfig.BUCKET_NAME), fileNameCaptor.capture());

        String capturedFileName = fileNameCaptor.getValue();
        assertEquals(EXPECTED_FILE_NAME, capturedFileName);
    }
}
