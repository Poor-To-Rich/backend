package com.poortorich.s3.util;

import com.poortorich.s3.constants.S3Constants.FileUtils;
import java.util.UUID;
import org.springframework.web.multipart.MultipartFile;

public class S3FileUtils {

    private S3FileUtils() {
    }

    public static String generateUniqueFileName(MultipartFile file) {
        return generateUniqueFileName(file.getOriginalFilename());
    }

    public static String generateUniqueFileName(String originalFileName) {
        String extension = extractExtension(originalFileName);
        return UUID.randomUUID() + extension;
    }

    private static String extractExtension(String originalFileName) {
        return originalFileName.substring(
                originalFileName.lastIndexOf(FileUtils.EXTENSION_SEPARATOR)
        );
    }
}
