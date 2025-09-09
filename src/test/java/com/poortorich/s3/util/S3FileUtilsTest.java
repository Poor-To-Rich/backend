package com.poortorich.s3.util;

import com.poortorich.s3.constants.S3TestFile;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.web.multipart.MultipartFile;

public class S3FileUtilsTest {

    @Test
    void generateUniqueFileName_WithMultipartFile_ShouldReturnUniqueNameWithExtension() {
        MultipartFile testFile = S3TestFileGenerator.createJpegFile();

        String result = S3FileUtils.generateUniqueFileName(testFile);

        Assertions.assertTrue(result.endsWith(S3FileUtils.WEBP_EXTENSION));
        Assertions.assertTrue(result.length() > S3FileUtils.WEBP_EXTENSION.length());
    }

    @Test
    void generateUniqueFileName_WithString_ShouldReturnUniqueNameWithExtension() {
        String originalFileName = S3TestFile.PNG_FILE_NAME;

        String result = S3FileUtils.generateUniqueFileName(originalFileName);

        Assertions.assertTrue(result.endsWith(S3FileUtils.WEBP_EXTENSION));
        Assertions.assertTrue(result.length() > S3FileUtils.WEBP_EXTENSION.length());
    }

    @Test
    void generateUniqueFileName_WithoutExtension_ShouldReturnUniqueNameWithEmptyExtension() {
        String originalFileName = S3TestFile.FILE_WITHOUT_EXTENSION;

        String result = S3FileUtils.generateUniqueFileName(originalFileName);

        Assertions.assertTrue(result.contains(S3FileUtils.EXTENSION_SEPARATOR));
    }
}
