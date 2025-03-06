package PoorToRich.PoorToRich.global.response;

import lombok.Builder;
import lombok.Getter;
import org.springframework.http.ResponseEntity;

@Builder
@Getter
public class BaseResponse {

    private final Integer resultCode;

    private final String resultMessage;

    public static ResponseEntity<BaseResponse> toResponseEntity(Response response) {
        BaseResponse baseResponse = BaseResponse.builder().resultCode(response.getHttpStatus().value())
                .resultMessage(response.getMessage()).build();

        return ResponseEntity.status(response.getHttpStatus()).body(baseResponse);
    }
}
