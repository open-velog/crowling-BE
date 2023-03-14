package com.example.finding.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;

@Slf4j
@Component
@Lazy
@PropertySource("classpath:/application-dev.properties")
public class NaverApiUtils {
    private RestTemplate rest;

    private HttpHeaders headers;

    private HttpEntity<String> requestEntity;

    @Value("${NAVER_CLIENT_ID}")
    private String naverClientId;

    @Value("${NAVER_API_URL}")
    private String naverTagetApiUrl;

    @Value("${NAVER_CLIENT_SECRET}")
    private String naverClientSecret;

    NaverApiUtils() {
        rest = new RestTemplate();
        headers = new HttpHeaders();
        headers.add("X-Naver-Client-Id", naverClientId);
        headers.add("X-Naver-Client-Secret", naverClientSecret);
        requestEntity = new HttpEntity("", headers);
    }

    public ResponseEntity<String> getSearchResultsByKeyword(String keyword) {
        return rest.exchange(naverTagetApiUrl + keyword, HttpMethod.GET, requestEntity, String.class);
    }
}
