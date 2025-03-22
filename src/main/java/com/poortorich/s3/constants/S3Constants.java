package com.poortorich.s3.constants;


import java.util.Arrays;
import java.util.List;

public class S3Constants {

    private S3Constants() {
    }

    public static final class Config {
        public static final String BUCKET = "${cloud.aws.s3.bucket}";
        public static final String REGION = "${cloud.aws.region.static}";
        public static final String ACCESS_KEY = "${cloud.aws.credentials.accessKey}";
        public static final String SECRET_KEY = "${cloud.aws.credentials.secretKey}";
    }

    public static final class ValidationMessages {
        public static final String INVALID_FILE_TYPE = "지원되지 않는 이미지 형식입니다. JPG, JPEG, PNG 파일만 허용됩니다.";
        public static final String FILE_SIZE_INVALID = "파일 크기가 너무 큽니다. 5MB 이하의 파일만 허용됩니다.";
        public static final String FILE_UPLOAD_FAILURE = "파일 업로드에 실패했습니다.";
    }

    public static final class ValidationConstraints {
        public static final List<String> ALLOWED_FILE_TYPES = Arrays.asList(
                "image/jpeg", "image/jpg", "image/png"
        );

        public static final long FILE_MAX_SIZE = 5 * 1024 * 1024;
    }

    public static final class FileUtils {
        public static final String EXTENSION_SEPARATOR = ".";
        public static final String PATH_SEPARATOR = "/";

        public static final class Index {
            public static final int NEXT_ELEMENT = 1;
        }
    }
}
