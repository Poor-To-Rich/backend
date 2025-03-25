package com.poortorich.s3.util;

import java.util.UUID;
import org.springframework.web.multipart.MultipartFile;

public class S3FileUtils {

    public static final String EXTENSION_SEPARATOR = ".";
    public static final String PATH_SEPARATOR = "/";
    public static final int NEXT_CHARACTER_OFFSET = 1;

    private S3FileUtils() {
    }

    public static String generateUniqueFileName(MultipartFile file) {
        return generateUniqueFileName(file.getOriginalFilename());
    }

    public static String generateUniqueFileName(String originalFileName) {
        String extension = extractExtension(originalFileName);
        return UUID.randomUUID() + extension;
    }
    
    public static String extractFileNameFromUrl(String urlString) {
        return urlString.substring(
                urlString.lastIndexOf(PATH_SEPARATOR) + NEXT_CHARACTER_OFFSET
        );
    }

    private static String extractExtension(String originalFileName) {
        try {
            return originalFileName.substring(
                    originalFileName.lastIndexOf(EXTENSION_SEPARATOR)
            );
        } catch (StringIndexOutOfBoundsException exception) {
            return originalFileName;
        }
    }
}
