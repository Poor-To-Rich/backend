package com.poortorich.s3.util;

import com.poortorich.global.exceptions.InternalServerErrorException;
import com.poortorich.s3.response.enums.S3Response;
import com.sksamuel.scrimage.ImmutableImage;
import com.sksamuel.scrimage.webp.WebpWriter;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
@RequiredArgsConstructor
public class WebpConverter {

    public byte[] convertToWebp(MultipartFile file) {
        try {
            return ImmutableImage.loader().fromBytes(file.getBytes())
                    .bytes(WebpWriter.DEFAULT.withQ(80));
        } catch (IOException exception) {
            throw new InternalServerErrorException(S3Response.FILE_UPLOAD_FAILURE);
        }
    }
}
