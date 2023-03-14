package com.example.finding.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

@Component
@Slf4j
public class CsvImportUtils {
    public void loadCsvFile() {
        String url = "jdbc:mysql://localhost:3306/keyword";
        String user = "user";
        String password = "password";
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
