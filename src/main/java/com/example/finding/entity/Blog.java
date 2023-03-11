package com.example.finding.entity;

import com.example.finding.dto.ItemsDto;
import lombok.*;

import javax.persistence.*;

@Entity(name = "blog")
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Blog extends TimeStamped{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String title;
    @Column(nullable = false)
    private String link;
    @Column(nullable = false)
    private String content;

    public static Blog create(ItemsDto itemsDto) {
        return Blog.builder()
                .title(itemsDto.getTitle())
                .link(itemsDto.getLink())
                .content(itemsDto.getContent())
                .build();
    }


}