package com.example.finding.utils;

import com.example.finding.dto.ItemsDto;
import com.example.finding.entity.Board;
import com.example.finding.repository.BoardRepository;
import com.example.finding.repository.KeywordRepository;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;

@Slf4j
@Component
@Lazy
public class CsvUtils {
    private boolean makeInitialCsvDirectory() {
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

    public void endCsvWriter(FileWriter csvWriter) {
        try {
            csvWriter.flush();
            csvWriter.close();
        } catch (IOException e) {
            log.error("Failed to close csvWriter safely");
            e.printStackTrace();
        }
    }

    public FileWriter getNewCsvFileWriter() throws FileNotFoundException {
        makeInitialCsvDirectory();

        for (int i = 1; i <= 10000; ++i) {
            String filePath = "./csv-files/test" + i + ".csv";
            File file = new File(filePath);

            if (file.exists()) {
                log.debug(filePath + " file exists");
                try {
                    return new FileWriter(file, Charset.forName("UTF-8"));
                } catch (IOException e) {
                    log.error("Can't make FileWriter of csv");
                }
            } else {
                log.debug(filePath + " file does not exist");
            }
        }

        throw new FileNotFoundException();
    }

    public void writeOneLineToCsv(FileWriter csvWriter, String... words) throws IOException {
        for (int i = 0; i < words.length; ++i) {
            if (i < words.length - 1) {
                csvWriter.append(words[i] + ",");
            }
            else {
                csvWriter.append(words[i] + "\n");
            }
        }
    }
}
