package com.example.finding.entity;

import com.example.finding.dto.ItemsDto;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity(name = "board")
//@Table(indexes = @Index(name = "link_index", columnList = "link", unique = false))
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Board extends TimeStamped {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 500, unique = true)
    private String link;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String content;

    public static Board create(ItemsDto itemsDto) {
        return new Board(itemsDto.getTitle(), itemsDto.getLink(), itemsDto.getContent());
    }

    public Board(String title, String link, String content) {
        this.title = title;
        this.link = link;
        this.content = content;
        this.modifiedAt = LocalDateTime.now();
    }
}