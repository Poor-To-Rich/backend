package com.poortorich.auth.kakao.service;

import com.poortorich.global.exceptions.InternalServerErrorException;
import com.poortorich.global.response.enums.GlobalResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class KakaoService {

    private static final String USER_SUFFIX = "kakao_";

    private final WebClient webClient = WebClient.builder()
            .baseUrl("https://kapi.kakao.com")
            .build();

    @Value("${kakao.admin.key}")
    private String KAKAO_ADMIN_KEY;

    public Mono<String> callUnlinkAPI(String username) {
        if (isKakaoUser(username)) {
            MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
            formData.add("target_id_type", "user_id");
            formData.add("target_id", username.substring(USER_SUFFIX.length()));

            return webClient.post()
                    .uri("/v1/user/unlink")
                    .header("Authorization", "KakaoAK " + KAKAO_ADMIN_KEY)
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .body(BodyInserters.fromFormData(formData))
                    .retrieve()
                    .onStatus(
                            status -> status.is4xxClientError() || status.is5xxServerError(),
                            response -> response.bodyToMono(String.class)
                                    .flatMap(errorBody -> Mono.error(
                                            new InternalServerErrorException(GlobalResponse.INTERNAL_SERVER_EXCEPTION)))
                    )
                    .bodyToMono(String.class);
        }
        return Mono.empty();
    }

    private boolean isKakaoUser(String username) {
        return username.contains(USER_SUFFIX);
    }
}
