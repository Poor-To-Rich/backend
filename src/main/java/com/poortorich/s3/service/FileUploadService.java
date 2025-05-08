package com.poortorich.s3.service;

import com.amazonaws.AmazonClientException;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.poortorich.global.exceptions.InternalServerErrorException;
import com.poortorich.s3.constants.S3Constants;
import com.poortorich.s3.response.enums.S3Response;
import com.poortorich.s3.util.S3FileUtils;
import com.poortorich.s3.validator.FileValidator;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class FileUploadService {


    private final AmazonS3Client amazonS3Client;
    private final FileValidator fileValidator;

    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;

    public String uploadImage(MultipartFile imageFile) {
        if (imageFile.isEmpty()) {
            return S3Constants.DEFAULT_PROFILE_IMAGE;
        }

        fileValidator.validateFileType(imageFile);
        fileValidator.validateFileSize(imageFile);

        String fileName = S3FileUtils.generateUniqueFileName(imageFile);

        uploadToS3(imageFile, fileName);

        return amazonS3Client.getUrl(bucketName, fileName).toString();
    }

    public String updateImage(String currentProfileImage, MultipartFile newProfileImage, Boolean isDefaultProfile) {
        if (isDefaultProfile) {
            deleteImage(currentProfileImage);
            return S3Constants.DEFAULT_PROFILE_IMAGE;
        }

        if (newProfileImage.isEmpty()) {
            return currentProfileImage;
        }

        deleteImage(currentProfileImage);
        return uploadImage(newProfileImage);
    }

    public void deleteImage(String imageUrl) {
        if (!Objects.equals(S3Constants.DEFAULT_PROFILE_IMAGE, imageUrl)) {
            String fileName = S3FileUtils.extractFileNameFromUrl(imageUrl);
            amazonS3Client.deleteObject(bucketName, fileName);
        }
    }

    private void uploadToS3(MultipartFile file, String fileName) {
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(file.getContentType());
        metadata.setContentLength(file.getSize());

        try (InputStream inputStream = file.getInputStream()) {
            amazonS3Client.putObject(new PutObjectRequest(bucketName, fileName, inputStream, metadata));
        } catch (AmazonClientException | IOException exception) {
            throw new InternalServerErrorException(S3Response.FILE_UPLOAD_FAILURE);
        }
    }
}
