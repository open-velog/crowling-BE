package com.example.finding.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity(name = "blog")
@Getter
@Setter
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
    private String descriptions;

    public Blog (String title, String link, String description) {
        this.title = title;
        this.link = link;
        this.descriptions = description;
    }

}