package com.example.finding.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.json.JSONObject;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.Pattern;

@Getter
@NoArgsConstructor

public class ItemsDto {
    private String title;
    private String link;
    private String description;

    public ItemsDto(JSONObject itemJson) {
        this.title = itemJson.getString("title");
        this.link = itemJson.getString("link");
        this.description = itemJson.getString("description");
    }
}
