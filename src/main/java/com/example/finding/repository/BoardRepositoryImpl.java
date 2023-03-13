package com.example.finding.repository;

import com.example.finding.entity.Board;
import com.example.finding.entity.QBoard;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.hibernate.query.NativeQuery;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.*;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class BoardRepositoryImpl implements BoardRepositoryCustom {
    private final EntityManager em;
//    private final JPAQueryFactory queryFactory;

    @Override
    @Transactional
    public void insertIgnoreLinkDuplication(List<Board> boards) {
        String sql = "INSERT IGNORE INTO board(link,title,content) VALUES ";
        String values = boards.stream()
                .map(board -> String.format("('%s','%s','%s')", board.getLink(), board.getTitle(), board.getContent()))
                .collect(Collectors.joining(","));

        Query query = em.createNativeQuery(sql + values);
        query.executeUpdate();
    }
}
