package com.sparta.hanhaeblog.repository;

import com.sparta.hanhaeblog.entity.Like;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LikeRepository extends JpaRepository<Like, Long> {
    Like findByPostIdAndUser_Id(Long postId, Long userId);
    Like findByCommentIdAndUser_Id(Long commentId, Long userId);
    void deleteAllByCommentId(Long commentId);
    void deleteAllByPostId(Long postId);
}
