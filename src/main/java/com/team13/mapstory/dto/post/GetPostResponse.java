package com.team13.mapstory.dto.post;

import com.team13.mapstory.entity.Category;
import com.team13.mapstory.entity.Emotion;
import com.team13.mapstory.entity.enums.CategoryEnum;
import com.team13.mapstory.entity.enums.EmotionEnum;
import com.team13.mapstory.entity.enums.IsPublicEnum;
import com.team13.mapstory.entity.enums.PersonEnum;
import com.team13.mapstory.entity.only.CategoryOnly;
import com.team13.mapstory.entity.only.EmotionOnly;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class GetPostResponse {

    private Long id;
    private String image;
    private LocalDateTime uploadTime;
    private double latitude;
    private double longitude;
    private CategoryOnly categoryOnly;
    private CategoryEnum category;
    private Long userId;
    private EmotionOnly emotionOnly;
    private EmotionEnum emotion;
    private PersonEnum person;
    private String content;
    private IsPublicEnum isPublic;
    private List<String> images;
}
