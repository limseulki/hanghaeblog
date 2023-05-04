package com.sparta.hanhaeblog.repository;

import com.sparta.hanhaeblog.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long> {
    Page<Post> findAll(Pageable pageable);
    List<Post> findAllByTitleContaining(String keyword);
    Optional<Post> findByIdAndUsername(Long id, String username);
}
