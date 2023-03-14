package com.example.finding.controller;

import com.example.finding.service.NaverApiService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class NaverApiController {

    private final NaverApiService naverApiService;

    @GetMapping("/search/csv")
    public void writeCSV()  {
        naverApiService.transferSerachResultsToCsv();
    }

    @GetMapping("/search/database")
    public void searchItems()  {
        naverApiService.transferSearchResultsToDatabase();
    }
}