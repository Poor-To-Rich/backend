package com.poortorich.s3.constants;

import java.util.List;

public class S3ValidationConstraints {

    private S3ValidationConstraints() {
    }

    public static final List<String> ALLOWED_FILE_TYPES = List.of(
            "image/jpeg", "image/jpg", "image/png"
    );

    public static final long FILE_MAX_SIZE = 5 * 1024 * 1024;
}
