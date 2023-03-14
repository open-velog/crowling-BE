package com.example.finding.utils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

@Slf4j
@Component
@Lazy
@RequiredArgsConstructor
public class CsvImportUtils {

    @Value("${spring.datasource.url}")
    private String datasourceUrl;

    @Value("${spring.datasource.username}")
    private String datasourceName;

    @Value("${spring.datasource.password}")
    private String datasourcePassword;

    public void loadCsvFile() {
        String url = datasourceUrl;
        String user = datasourceName;
        String password = datasourcePassword;
        String query = "LOAD DATA INFILE ? " +
                "IGNORE INTO TABLE board " +
                "FIELDS TERMINATED BY ',' ENCLOSED BY '\"' " +
                "LINES TERMINATED BY '\\n' " +
                "(link, title, content)";
        try (Connection conn = DriverManager.getConnection(url, user, password);
             PreparedStatement stmt = conn.prepareStatement(query)) {
            File csvDir = new File("./csv-files");
            File[] csvFiles = csvDir.listFiles();
            for(File csv : csvFiles) {
                log.info("삽입 시작: {}", csv.getName());
                stmt.setString(1, csv.getAbsolutePath());
                stmt.executeUpdate();
                log.info("삽입 끝: {}", csv.getName());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
