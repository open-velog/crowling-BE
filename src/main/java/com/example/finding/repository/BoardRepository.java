package com.example.finding.repository;

import com.example.finding.entity.Board;
import org.hibernate.annotations.SQLInsert;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.hibernate.hql.internal.antlr.HqlTokenTypes.NOT;
import static org.hibernate.loader.Loader.SELECT;
import static org.hibernate.sql.ast.Clause.FROM;
import static org.hibernate.sql.ast.Clause.WHERE;

@Repository
public interface BoardRepository extends JpaRepository<Board, Long>, BoardRepositoryCustom{
    @Modifying
    @Query(value = "DELETE FROM board b1 " +
            "WHERE EXISTS ( " +
            "    SELECT 1 FROM board b2 " +
            "    WHERE b2.link = b1.link " +
            "    AND b2.id > b1.id " +
            ") ", nativeQuery = true)
    void deleteDuplicateLinks();


//    @Modifying
//    @Query(value = "INSERT INTO board(link, title, content) " +
//            "SELECT b.link, b.title, b.content FROM (VALUES (:#{#boards.![link]}, :#{#boards.![title]}, :#{#boards.![content]})) AS b(link, title, content) " +
//            "WHERE NOT EXISTS " +
//            "(SELECT 1 FROM board b2 WHERE b2.link = b.link)", nativeQuery = true)
//    void insertIgnoreLinkDuplication(@Param("boards") List<Board> boards);


//    @Query("INSERT INTO Board (link, title, content) " +
//            "SELECT ""link"" +
//            "FROM Board b " +
//            "WHERE b.link != '산토끼1'")
//    void insertIgnore(@Param("board") Board board);



//    void insertIgnoreBoards(@Param("boards") List<Board> boards);

//    @Transactional
//    @Modifying
//    @Query(value = "", nativeQuery = true)
//    void deleteDuplicateRows();

}
