package com.example.finding.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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
public class NaverApiUtils {
    private RestTemplate rest;

    private HttpHeaders headers;

    private HttpEntity<String> requestEntity;

    private String naverTagetApiUrl;

    private String naverClientSecret;

    private String naverClientId;

    @Autowired
    public NaverApiUtils(@Value("${naver.api.url}") String naverTagetApiUrl,
                         @Value("${naver.client.id}") String naverClientId,
                         @Value("${naver.client.secret}") String naverClientSecret) {
        this.naverTagetApiUrl = naverTagetApiUrl;
        this.naverClientSecret = naverClientSecret;
        this.naverClientId = naverClientId;

        this.headers = new HttpHeaders();
        this.headers.add("X-Naver-Client-Id", naverClientId);
        this.headers.add("X-Naver-Client-Secret", naverClientSecret);
        this.requestEntity = new HttpEntity("", headers);
        this.rest = new RestTemplate();
    }

    public ResponseEntity<String> getSearchResultsByKeyword(String keyword) {
        ResponseEntity<String> responseEntity = rest.exchange(naverTagetApiUrl + keyword, HttpMethod.GET, requestEntity, String.class);
        log.info("Naver Api status code: {}", responseEntity.getStatusCode());
        return responseEntity;
    }
}
