package com.sparta.hanhaeblog.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity(name = "Heart")
@NoArgsConstructor
public class Like {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = true)
    private Long postId;

    @Column(nullable = true)
    private Long commentId;

    public Like(User user, Long postId, Long commentId) {
        this.user = user;
        this.postId = postId;
        this.commentId = commentId;
    }
}
