package com.sparta.hanhaeblog.repository;

import com.sparta.hanhaeblog.entity.Image;
import com.sparta.hanhaeblog.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ImageRepository extends JpaRepository<Image, Long> {
}
