package com.poortorich.s3.constants;

public class S3TestConfig {

    public static final String BUCKET_NAME = "test-bucket";
    public static final String URL_BASE = "https://" + BUCKET_NAME + ".s3.amazonaws.com/";
    public static final String FILE_URL_SAMPLE_1 = URL_BASE + "test-file.jpg";
    public static final String FILE_URL_SAMPLE_2 = URL_BASE + "sample-2-test-file.jpg";
}
