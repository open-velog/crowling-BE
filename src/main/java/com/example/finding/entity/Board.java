package com.example.finding.entity;

import com.example.finding.dto.ItemsDto;
import lombok.*;

import javax.persistence.*;

@Entity(name = "board")
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Board extends TimeStamped{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String title;
    @Column(nullable = false)
    private String link;
    @Column(nullable = false)
    private String content;

    public static Board create(ItemsDto itemsDto) {
        return Board.builder()
                .title(itemsDto.getTitle())
                .link(itemsDto.getLink())
                .content(itemsDto.getContent())
                .build();
    }
    public Board (String title, String link, String content){
        this.title = title;
        this.link = link;
        this.content = content;
    }


}