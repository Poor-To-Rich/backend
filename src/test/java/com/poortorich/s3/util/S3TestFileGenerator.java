package com.poortorich.s3.util;

import com.poortorich.s3.constants.S3TestFile;
import org.springframework.mock.web.MockMultipartFile;

public final class S3TestFileGenerator {

    private S3TestFileGenerator() {
    }

    public static MockMultipartFile createJpegFile() {
        return new MockMultipartFile(
                S3TestFile.FILE_NAME,
                S3TestFile.JPEG_FILE_NAME,
                S3TestFile.JPEG_CONTENT_TYPE,
                new byte[S3TestFile.ONE_MB]
        );
    }

    public static MockMultipartFile createPngFile() {
        return new MockMultipartFile(
                S3TestFile.FILE_NAME,
                S3TestFile.PNG_FILE_NAME,
                S3TestFile.PNG_CONTENT_TYPE,
                new byte[S3TestFile.ONE_MB]
        );
    }

    public static MockMultipartFile createPdfFile() {
        return new MockMultipartFile(
                S3TestFile.FILE_NAME,
                S3TestFile.PDF_FILE_NAME,
                S3TestFile.PDF_CONTENT_TYPE,
                new byte[S3TestFile.ONE_MB]
        );
    }

    public static MockMultipartFile createLargeFile() {
        return new MockMultipartFile(
                S3TestFile.FILE_NAME,
                S3TestFile.LARGE_FILE_NAME,
                S3TestFile.JPEG_CONTENT_TYPE,
                new byte[S3TestFile.SIX_MB]
        );
    }

    public static MockMultipartFile createEmpty() {
        return new MockMultipartFile(
                S3TestFile.FILE_NAME,
                S3TestFile.EMPTY_FILE_NAME,
                S3TestFile.JPEG_CONTENT_TYPE,
                new byte[S3TestFile.ZERO_MB]
        );
    }
}
