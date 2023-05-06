package com.sparta.hanhaeblog.dto;

import com.sparta.hanhaeblog.entity.Image;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ImageDto {

    private String originImageName;
    private String imageName;
    private String imagePath;

    @Builder
    public ImageDto (String originImageName, String imageName, String imagePath) {
        this.originImageName = originImageName;
        this.imageName = imageName;
        this.imagePath = imagePath;
    }
}
