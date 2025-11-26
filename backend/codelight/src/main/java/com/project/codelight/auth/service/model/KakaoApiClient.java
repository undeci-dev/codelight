package com.project.codelight.auth.service.model;

import com.project.codelight.auth.config.KakaoOAuthProperties;
import com.project.codelight.auth.dto.response.KakaoTokenResponse;
import com.project.codelight.auth.dto.response.KakaoUserInfoResponse;
import com.project.codelight.global.exception.CodeLightException;
import com.project.codelight.global.exception.ExceptionCodeType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

@Slf4j
@Component
@RequiredArgsConstructor
public class KakaoApiClient {

    private final KakaoOAuthProperties properties;
    private final RestClient restClient;

    public KakaoTokenResponse getAccessToken(String code) {
        MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
        requestBody.add("grant_type", properties.getGrantType());
        requestBody.add("client_id", properties.getClientId());
        requestBody.add("client_secret", properties.getClientSecret());
        requestBody.add("redirect_uri", properties.getRedirectUri());
        requestBody.add("code", code);

        return restClient.post()
            .uri(properties.getTokenUri())
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .body(requestBody)
            .retrieve()
            .onStatus(HttpStatusCode::is4xxClientError, (request, response) -> {
                throw new CodeLightException(ExceptionCodeType.OAUTH_TOKEN_REQUEST_FAILED);
            })
            .onStatus(HttpStatusCode::is5xxServerError, (request, response) -> {
                throw new CodeLightException(ExceptionCodeType.OAUTH_SERVER_ERROR);
            })
            .body(KakaoTokenResponse.class);
    }

    public KakaoUserInfoResponse getUserInfo(String accessToken) {
        return restClient.get()
            .uri(properties.getUserInfoUri())
            .header("Authorization", "Bearer " + accessToken)
            .retrieve()
            .onStatus(HttpStatusCode::is4xxClientError, (request, response) -> {
                throw new CodeLightException(ExceptionCodeType.OAUTH_USER_INFO_REQUEST_FAILED);
            })
            .onStatus(HttpStatusCode::is5xxServerError, (request, response) -> {
                throw new CodeLightException(ExceptionCodeType.OAUTH_SERVER_ERROR);
            })
            .body(KakaoUserInfoResponse.class);
    }
}
