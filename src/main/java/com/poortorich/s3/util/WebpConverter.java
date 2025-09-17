package com.poortorich.s3.util;

import com.poortorich.global.exceptions.InternalServerErrorException;
import com.poortorich.s3.response.enums.S3Response;
import com.sksamuel.scrimage.ImmutableImage;
import com.sksamuel.scrimage.webp.WebpWriter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class WebpConverter {

    private boolean isWebp(byte[] bytes) {
        if (bytes == null || bytes.length < 12) return false;
        return bytes[0] == 'R' && bytes[1] == 'I' && bytes[2] == 'F' && bytes[3] == 'F'
                && bytes[8] == 'W' && bytes[9] == 'E' && bytes[10] == 'B' && bytes[11] == 'P';
    }

    public byte[] convertToWebp(MultipartFile file) {
        try {
            String contentType = file.getContentType();
            byte[] bytes = file.getBytes();
            if ("image/webp".equalsIgnoreCase(contentType) && isWebp(bytes)) {
                return bytes;
            }

            return ImmutableImage.loader().fromBytes(file.getBytes())
                    .bytes(WebpWriter.DEFAULT.withQ(80));
        } catch (IOException exception) {
            throw new InternalServerErrorException(S3Response.FILE_UPLOAD_FAILURE);
        }
    }
}
