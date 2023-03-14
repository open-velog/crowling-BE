package com.example.finding.service;

import com.example.finding.entity.Keyword;
import com.example.finding.repository.KeywordRepository;
import lombok.RequiredArgsConstructor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class KeywordService {

    private final KeywordRepository keywordRepository;

    public void getLimitedCoin() {
        try {
            List<Keyword> keywordList = new ArrayList<>();
            for (int i = 300; i < 462; i++) {

                Document doc = Jsoup.connect("https://terms.naver.com/list.naver?cid=67995&categoryId=67995&page=" + i).get();
                Elements informations = doc.select("div.list_wrap > ul > li");
                Elements keywords = informations.select("div.info_area > div.subject > strong > a:nth-child(1)");

                for(Element element : keywords) {
                    String keyword;
                    if(element.text().length() > 1) {
                        keyword = element.text().substring(0,2);
                    }else {
                        keyword = element.text();
                    }
                    keywordList.add(Keyword.create(keyword));
                }

            }
            keywordRepository.saveAll(keywordList);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}