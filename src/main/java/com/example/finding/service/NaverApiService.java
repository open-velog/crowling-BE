package com.example.finding.service;

import com.example.finding.entity.Blog;
import com.example.finding.repository.BlogRepository;
import com.example.finding.dto.ItemsDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class NaverApiService {
    private final BlogRepository blogRepository;
    @Transactional
    public List<ItemsDto> searchItems(String query) {

        RestTemplate rest = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Naver-Client-Id", "wyCvLvhOWDeW6BC20eNt");
        headers.add("X-Naver-Client-Secret", "gWTeNvJoWW");
        String body = "";

        HttpEntity<String> requestEntity = new HttpEntity<String>(body, headers);
        ResponseEntity<String> responseEntity = rest.exchange("https://openapi.naver.com/v1/search/blog.json?display=100&query=" + query , HttpMethod.GET, requestEntity, String.class);

        HttpStatus httpStatus = responseEntity.getStatusCode();
        int status = httpStatus.value();
        log.info("NAVER API Status Code : " + status);

        String response = responseEntity.getBody();

        return fromJSONtoItems(response);
    }

    //파싱 파트
    public List<ItemsDto> fromJSONtoItems(String response) {

        JSONObject rjson = new JSONObject(response);
        JSONArray items  = rjson.getJSONArray("items");
        List<ItemsDto> itemsDtoList = new ArrayList<>();

        for (int i=0; i<items.length(); i++) {
            JSONObject itemJson = items.getJSONObject(i);
            ItemsDto itemsDto = new ItemsDto(itemJson);
            Blog blog = new Blog(itemsDto.getTitle().replaceAll("<[^>]*>", " "),
                    itemsDto.getLink(), itemsDto.getDescription().replaceAll("<[^>]*>", " "));
            blogRepository.save(blog);
            itemsDtoList.add(itemsDto);
        }

        return itemsDtoList;
    }


}