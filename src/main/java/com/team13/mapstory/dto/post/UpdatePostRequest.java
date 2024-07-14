package com.team13.mapstory.dto.post;

import com.team13.mapstory.entity.enums.CategoryEnum;
import com.team13.mapstory.entity.enums.EmotionEnum;
import com.team13.mapstory.entity.enums.IsPublicEnum;
import com.team13.mapstory.entity.enums.PersonEnum;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public class UpdatePostRequest {

    private String imageUrl;
    private LocalDateTime uploadTime;
    private double latitude;
    private double longitude;
    private CategoryEnum category;
    private EmotionEnum emotion;
    private PersonEnum person;
    private String content;
    private IsPublicEnum isPublic;
    private List<String> deleteImages;
    private List<String> addImages;
}
