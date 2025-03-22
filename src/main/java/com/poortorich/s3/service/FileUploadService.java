package com.poortorich.s3.service;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.poortorich.global.exceptions.InternalServerErrorException;
import com.poortorich.s3.constants.S3Constants.Config;
import com.poortorich.s3.constants.S3Constants.FileUtils;
import com.poortorich.s3.constants.S3Constants.FileUtils.Index;
import com.poortorich.s3.response.enums.S3Response;
import com.poortorich.s3.util.S3FileUtils;
import com.poortorich.s3.validator.FileValidator;
import java.io.IOException;
import java.io.InputStream;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class FileUploadService {

    private final AmazonS3Client amazonS3Client;

    private final FileValidator fileValidator;

    @Value(Config.BUCKET)
    private String bucketName;

    public String uploadImage(MultipartFile imageFile) {
        fileValidator.validatoeFileType(imageFile);
        fileValidator.validateFileSize(imageFile);

        String fileName = S3FileUtils.generateUniqueFileName(imageFile);

        uploadFileToS3(imageFile, fileName);

        return amazonS3Client.getUrl(bucketName, fileName).toString();
    }

    public void deleteImage(String imageUrl) {
        String fileName = imageUrl.substring(
                imageUrl.lastIndexOf(FileUtils.PATH_SEPARATOR) + Index.NEXT_ELEMENT
        );
        amazonS3Client.deleteObject(bucketName, fileName);
    }

    private void uploadFileToS3(MultipartFile file, String fileName) {
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(file.getContentType());
        metadata.setContentLength(file.getSize());

        try (InputStream inputStream = file.getInputStream()) {
            amazonS3Client.putObject(new PutObjectRequest(bucketName, fileName, inputStream, metadata));
        } catch (IOException exception) {
            throw new InternalServerErrorException(S3Response.FILE_UPLOAD_FAILURE);
        }
    }
}
