package com.team13.mapstory.dto.post;

import com.team13.mapstory.entity.enums.EmotionEnum;
import com.team13.mapstory.entity.enums.IsPublicEnum;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class GetsPostResponse {

    private Long id;
    private String image;
    private LocalDateTime uploadTime;
    private double latitude;
    private double longitude;
    private Long userId;
    private EmotionEnum emotion;
    private IsPublicEnum isPublic;
}
