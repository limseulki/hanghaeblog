package com.sparta.hanhaeblog.repository;

import com.sparta.hanhaeblog.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {
}
