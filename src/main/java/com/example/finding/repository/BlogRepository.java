package com.example.finding.repository;

import com.example.finding.entity.Blog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BlogRepository extends JpaRepository<Blog, Long> {
   // Optional<Blog> findByBlog(Blog blog);

}
