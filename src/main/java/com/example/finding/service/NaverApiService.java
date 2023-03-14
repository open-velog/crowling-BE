package com.example.finding.service;

import com.example.finding.constant.ConstantTable;
import com.example.finding.entity.Board;
import com.example.finding.entity.Keyword;
import com.example.finding.dto.ItemsDto;
import com.example.finding.repository.KeywordRepository;
import com.example.finding.utils.CsvUtils;
import com.example.finding.utils.NaverApiUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


@Slf4j
@Service
@RequiredArgsConstructor
public class NaverApiService {
    private final CsvUtils csvUtils;

    private final NaverApiUtils naverApiUtils;

    private final KeywordRepository keywordRepository;

    public void transferSerachResultsToCsv() {
        List<Keyword> keywordList = keywordRepository.findByIdGreaterThan(ConstantTable.START_KEYWORD_ID);
        int insertionCount = 0;

        try {
            FileWriter csvWriter = csvUtils.getNewCsvFileWriter();
            csvUtils.writeOneLineToCsv(csvWriter, "link", "title", "content");    // set column

            for (Keyword keyword : keywordList) {
                log.info("Insertion: keyword_id={}, keyword={}, insertion count={}", keyword.getId(), keyword.getKeyword(), insertionCount);
                ResponseEntity<String> responseEntity = naverApiUtils.getSearchResultsByKeyword(keyword.getKeyword());

                // Naver Api blocks requesting too often
                // So added interval for every request
                try {
                    Thread.sleep(ConstantTable.INTERVAL);
                } catch (InterruptedException e) {

                }

                String response = responseEntity.getBody();
                List<Board> fetchedBoards = getBoardsFromNaverApiResponse(response);
                for (Board fetchedBoard : fetchedBoards) {
                    // row insert
                    csvUtils.writeOneLineToCsv(
                            csvWriter, fetchedBoard.getLink(), fetchedBoard.getTitle(), fetchedBoard.getContent()
                    );
                    ++insertionCount;
                }

                if (insertionCount >= ConstantTable.BATCH_SIZE) {
                    log.info("Last insertion: keyword_id={}, keyword={}, insertion count={}", keyword.getId(), keyword.getKeyword(), insertionCount);
                    insertionCount = 0;
                }

                if (insertionCount == 0) {
                    csvUtils.endCsvWriter(csvWriter);
                    csvWriter = csvUtils.getNewCsvFileWriter();
                }
            }

            csvUtils.endCsvWriter(csvWriter);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Extracts boards from Naver Api's response
    public List<Board> getBoardsFromNaverApiResponse(String response) {
        List<Board> fetchedBoards = new ArrayList<>();
        try {
            JSONObject rjson = new JSONObject(response);
            JSONArray items = rjson.getJSONArray("items");
            for (int i = 0; i < items.length(); i++) {
                JSONObject itemJson = items.getJSONObject(i);
                ItemsDto itemsDto = new ItemsDto(itemJson);
                if (itemsDto.getLink().length() >= 500) {
                    continue;
                }
                fetchedBoards.add(Board.create(itemsDto));
            }
        } catch (JSONException e) {
            log.error("Wrong Json");
        } catch (Exception e) {
            e.printStackTrace();
        }

        return fetchedBoards;
    }

//    needed to track boards' identification via its link
//    HashSet<String> getUniqueLinkSetFromDatabase() {
//        HashSet<String> linkSet = new HashSet();
//        List<Board> allBoardsAtFirst = boardRepository.findAll();
//        for (Board board : allBoardsAtFirst) {
//            linkSet.add(board.getLink());
//        }
//        return linkSet;
//    }

    @Transactional
    public void transferSearchResultsToDatabase() {
//        RestTemplate rest = new RestTemplate();
//        HttpHeaders headers = new HttpHeaders();
//        headers.add("X-Naver-Client-Id", "wyCvLvhOWDeW6BC20eNt");
//        headers.add("X-Naver-Client-Secret", "B9xPFn77Rv");
//        String body = "";
//
//        List<Keyword> keywordList = keywordRepository.findByIdBetween(ConstantTable.START_KEYWORD_ID, ConstantTable.BATCH_SIZE);
//        HashSet<String> linkSet = getUniqueLinkSetFromDatabase();
//
//        List<Board> batchBoards = new ArrayList<>((int)ConstantTable.BATCH_SIZE);
//        for (Keyword keyword:keywordList) {
//            HttpEntity<String> requestEntity = new HttpEntity<>(body, headers);
//            ResponseEntity<String> responseEntity = rest.exchange("https://openapi.naver.com/v1/search/blog.json?display=100&query=" + keyword.getKeyword(), HttpMethod.GET, requestEntity, String.class);
//
//            HttpStatus httpStatus = responseEntity.getStatusCode();
//            int status = httpStatus.value();
//            log.info("NAVER API Status Code : {} keyword's id : {}, keyword: {} batchBoard's size : {}",
//                    status, keyword.getId(), keyword.getKeyword(), batchBoards.size());
//
//            // API 너무 빠르게 호출하면 Ban 먹기때문에, 일부러 interval 를 두었음
//            try {
//                Thread.sleep(ConstantTable.INTERVAL);
//            }
//            catch (InterruptedException e) {
//
//            }
//
//            String response = responseEntity.getBody();
//            fromJSONtoItems(response, batchBoards, linkSet);
//
//            if (batchBoards.size() > ConstantTable.BATCH_SIZE) {
//                log.debug("added at last keyword: " + keyword);
//                boardRepository.saveAll(batchBoards);
//                batchBoards.clear();
//            }
//        }
    }

}