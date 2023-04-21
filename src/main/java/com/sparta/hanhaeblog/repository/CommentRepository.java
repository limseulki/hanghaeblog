package com.sparta.hanhaeblog.repository;

import com.sparta.hanhaeblog.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findAllByPostIdOrderByCreatedAtDesc(Long id);
    void deleteAllByPostId(Long id);

}
