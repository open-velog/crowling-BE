package com.example.finding.service;

import com.example.finding.entity.Board;
import com.example.finding.entity.Keyword;
import com.example.finding.repository.BoardRepository;
import com.example.finding.dto.ItemsDto;
import com.example.finding.repository.KeywordRepository;
import com.example.finding.utils.DeduplicationUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class NaverApiService {
    private final BoardRepository boardRepository;

    private final KeywordRepository keywordRepository;
    @Transactional
    public void searchItems() {
        RestTemplate rest = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Naver-Client-Id", "wyCvLvhOWDeW6BC20eNt");
        headers.add("X-Naver-Client-Secret", "gWTeNvJoWW");
        String body = "";

        List<Keyword> keywordList = keywordRepository.findByIdBetween(2L,100L);
        for (Keyword keyword:keywordList){
            List<Board> boards=boardRepository.findAll();
            Set<String> links = new HashSet<>();
            for (Board board : boards){
                links.add(board.getLink());
            }
            HttpEntity<String> requestEntity = new HttpEntity<String>(body, headers);
            ResponseEntity<String> responseEntity = rest.exchange("https://openapi.naver.com/v1/search/blog.json?display=100&query=" + keyword.getKeyword(), HttpMethod.GET, requestEntity, String.class);

            HttpStatus httpStatus = responseEntity.getStatusCode();
            int status = httpStatus.value();
            log.info("NAVER API Status Code : " + status);

            String response = responseEntity.getBody();
            fromJSONtoItems(response, links);
        }
    }

    //파싱 파트
    public void fromJSONtoItems(String response, Set<String> links) {
        JSONObject rjson = new JSONObject(response);
        JSONArray items  = rjson.getJSONArray("items");
        List<Board> boardList = new ArrayList<>();
        log.info(""+items.length());
        for (int i=0; i<items.length(); i++) {
            JSONObject itemJson = items.getJSONObject(i);
            ItemsDto itemsDto = new ItemsDto(itemJson);
            if(!links.contains(itemsDto.getLink())){
                boardList.add(Board.create(itemsDto));
            }
        }
        boardList = DeduplicationUtils.deduplication(boardList,Board::getLink);
        boardRepository.saveAll(boardList);
    }


}