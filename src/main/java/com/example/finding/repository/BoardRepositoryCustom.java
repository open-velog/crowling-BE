package com.example.finding.repository;

import com.example.finding.entity.Board;
import org.springframework.stereotype.Repository;

import java.util.List;

public interface BoardRepositoryCustom {
    public void insertIgnoreLinkDuplication(List<Board> boards);
}