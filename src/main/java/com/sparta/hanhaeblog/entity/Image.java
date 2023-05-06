package com.sparta.hanhaeblog.entity;

import com.sparta.hanhaeblog.dto.ImageDto;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@NoArgsConstructor
public class Image {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long imageNo;

    @Column(length = 500, nullable = false)
    private String originImageName;

    @Column(length = 500, nullable = false)
    private String imageName;

    @Column(length = 1000, nullable = false)
    private String imagePath;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Builder
    public Image(ImageDto imageDto, User user) {
        this.originImageName = imageDto.getOriginImageName();
        this.imageName = imageDto.getImageName();
        this.imagePath = imageDto.getImagePath();
        this.user = user;
    }
}
