package com.example.finding.service;

import com.example.finding.constant.ConstantTable;
import com.example.finding.entity.Board;
import com.example.finding.entity.Keyword;
import com.example.finding.dto.ItemsDto;
import com.example.finding.repository.BoardRepository;
import com.example.finding.repository.KeywordRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;


@Slf4j
@Service
@RequiredArgsConstructor
public class NaverApiService {
    private final BoardRepository boardRepository;

    private final KeywordRepository keywordRepository;

    public boolean makeInitialCsvDirectory() {
        File directory = new File("./csv-files");

        boolean isSuccess = false;
        // Check if the directory already exists
        if (!directory.exists()) {
            // Attempt to create the directory and its parent directories
            isSuccess = directory.mkdirs();

            // Check if the directory was created successfully
            if (isSuccess) {
                log.debug("CSV Directory created successfully.");
            }
            else {
                log.debug("Failed to create CSV directory.");
            }
        }
        else {
            log.debug("CSV Failed to create CSV directory.");
        }

        return isSuccess;
    }

    public File getFileToWrite() throws FileNotFoundException {
        makeInitialCsvDirectory();

        for (int i = 1; i <= 10000; ++i) {
            String filePath = "./csv-files/test" + i + ".csv";
            File file = new File(filePath);

            if (file.exists()) {
                log.debug(filePath + " file exists");
            } else {
                log.debug(filePath + " file does not exist");
                return file;
            }
        }

        throw new FileNotFoundException();
    }

    public void setColumnOfCsv(FileWriter csvWriter) throws IOException {
        // write headers
        csvWriter.append("id");
        csvWriter.append(",");
        csvWriter.append("link");
        csvWriter.append(",");
        csvWriter.append("title");
        csvWriter.append(",");
        csvWriter.append("content");
        csvWriter.append("\n");
    }

    public void writeCSV() {
        RestTemplate rest = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Naver-Client-Id", "wyCvLvhOWDeW6BC20eNt");
        headers.add("X-Naver-Client-Secret", "B9xPFn77Rv");
        String body = "";

        List<Keyword> keywordList = keywordRepository.findByIdGreaterThan(ConstantTable.START_KEYWORD_ID);

        try {
            FileWriter csvWriter = new FileWriter(getFileToWrite(), Charset.forName("UTF-8"));
            setColumnOfCsv(csvWriter);
            long insertionCount = 0;

            for (Keyword keyword : keywordList) {
                HttpEntity<String> requestEntity = new HttpEntity(body, headers);
                ResponseEntity<String> responseEntity = rest.exchange("https://openapi.naver.com/v1/search/blog.json?display=100&query=" + keyword.getKeyword(), HttpMethod.GET, requestEntity, String.class);

                log.info("keyword_id : {}, keyword : {} insertion count : {}",
                        keyword.getId(), keyword.getKeyword(), insertionCount);

                // 너무 빨리 API 를 호출하면, Naver 에서 API를 호출하지 못하도록 막는다.
                // 고로, 인터벌을 두어서 API를 호출하기로 결정함.
                try {
                    Thread.sleep(ConstantTable.INTERVAL);
                }
                catch (InterruptedException e) {

                }

                String response = responseEntity.getBody();
                try {
                    JSONObject rjson = new JSONObject(response);
                    JSONArray items = rjson.getJSONArray("items");
                    for (int i = 0; i < items.length(); i++) {
                        JSONObject itemJson = items.getJSONObject(i);
                        ItemsDto itemsDto = new ItemsDto(itemJson);
                        if (itemsDto.getLink().length() >= 1000) {
                            continue;
                        }

                        writeOneLineToCsv(csvWriter, itemsDto);
                        insertionCount += 1;
                        if (insertionCount >= ConstantTable.BATCH_SIZE) {
                            insertionCount = 0;
                            csvWriter.flush();
                            csvWriter.close();
                            csvWriter = new FileWriter(getFileToWrite(), Charset.forName("UTF-8"));
                        }
                    }
                }

                catch (JSONException e) {
                    log.error("Wrong Json");
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }

            csvWriter.flush();
            csvWriter.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    void writeOneLineToCsv(FileWriter csvWriter, ItemsDto itemsDto) throws IOException {
        Board board = Board.create(itemsDto);
        csvWriter.append(String.valueOf(board.getId()));
        csvWriter.append(",");
        csvWriter.append(board.getTitle());
        csvWriter.append(",");
        csvWriter.append(board.getContent());
        csvWriter.append("\n");
    }

    HashSet<String> getUniqueLinkSetFromDatabase() {
        HashSet<String> linkSet = new HashSet();
        List<Board> allBoardsAtFirst = boardRepository.findAll();
        for (Board board : allBoardsAtFirst) {
            linkSet.add(board.getLink());
        }
        return linkSet;
    }

    @Transactional
    public void searchItems() {
        RestTemplate rest = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Naver-Client-Id", "wyCvLvhOWDeW6BC20eNt");
        headers.add("X-Naver-Client-Secret", "B9xPFn77Rv");
        String body = "";

        List<Keyword> keywordList = keywordRepository.findByIdBetween(ConstantTable.START_KEYWORD_ID, ConstantTable.BATCH_SIZE);
        HashSet<String> linkSet = getUniqueLinkSetFromDatabase();

        List<Board> batchBoards = new ArrayList<>((int)ConstantTable.BATCH_SIZE);
        for (Keyword keyword:keywordList) {
            HttpEntity<String> requestEntity = new HttpEntity<>(body, headers);
            ResponseEntity<String> responseEntity = rest.exchange("https://openapi.naver.com/v1/search/blog.json?display=100&query=" + keyword.getKeyword(), HttpMethod.GET, requestEntity, String.class);

            HttpStatus httpStatus = responseEntity.getStatusCode();
            int status = httpStatus.value();
            log.info("NAVER API Status Code : {} keyword's id : {}, keyword: {} batchBoard's size : {}",
                    status, keyword.getId(), keyword.getKeyword(), batchBoards.size());

            // API 너무 빠르게 호출하면 Ban 먹기때문에, 일부러 interval 를 두었음
            try {
                Thread.sleep(ConstantTable.INTERVAL);
            }
            catch (InterruptedException e) {

            }

            String response = responseEntity.getBody();
            fromJSONtoItems(response, batchBoards, linkSet);

            if (batchBoards.size() > ConstantTable.BATCH_SIZE) {
                log.debug("added at last keyword: " + keyword);
                boardRepository.saveAll(batchBoards);
                batchBoards.clear();
            }
        }
    }

    //파싱 파트
    public void fromJSONtoItems(String response, List<Board> batchBoards, HashSet<String> linkSet) {
        try {
            JSONObject rjson = new JSONObject(response);
            JSONArray items = rjson.getJSONArray("items");
//            log.info("" + items.length());
            for (int i = 0; i < items.length(); i++) {
                JSONObject itemJson = items.getJSONObject(i);
                ItemsDto itemsDto = new ItemsDto(itemJson);
                if (itemsDto.getLink().length() >= 500 || linkSet.contains(itemsDto.getLink())) {
                    continue;
                }
                linkSet.add(itemsDto.getLink());
                batchBoards.add(Board.create(itemsDto));
            }
        } catch (JSONException e) {
            log.error("Wrong Json");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}