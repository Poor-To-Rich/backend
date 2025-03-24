package com.poortorich.s3.constants;

public class S3ResponseMessages {

    public static final String INVALID_FILE_TYPE = "지원되지 않는 이미지 형식입니다. JPG, JPEG, PNG 파일만 허용됩니다.";
    public static final String FILE_SIZE_INVALID = "파일 크기가 너무 큽니다. 5MB 이하의 파일만 허용됩니다.";
    public static final String FILE_UPLOAD_FAILURE = "파일 업로드 중 문제가 발생했습니다. 다시 시도해 주세요.";

    private S3ResponseMessages() {
    }

}
