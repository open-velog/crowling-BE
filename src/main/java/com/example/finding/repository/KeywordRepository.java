package com.example.finding.repository;

import com.example.finding.entity.Keyword;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface KeywordRepository extends JpaRepository<Keyword, Long> {
    List<Keyword> findByIdBetween(Long id1, Long id2);
}
