package com.team13.mapstory.entity;

import com.team13.mapstory.entity.enums.CategoryEnum;
import com.team13.mapstory.entity.enums.EmotionEnum;
import com.team13.mapstory.entity.enums.IsPublicEnum;
import com.team13.mapstory.entity.enums.PersonEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @Schema(description = "대표 이미지 URL 주소", example = "https://map-story-team13.s3.ap-northeast-2.amazonaws.com/d5aaba88-4690-42cc-8f92-ec200253ad0b_aws.png")
    private String image;

    @Column(nullable = false)
    @Schema(description = "사진 찍은 시간 (없을 시에 업로드 시간)", example = "2024-07-04T12:33:27")
    private LocalDateTime upload_time;

//    @ManyToOne
//    @JoinColumn(name = "locate_id", referencedColumnName = "id", nullable = false)
//    private Location location;

    @Column(nullable = false)
    private double latitude;

    @Column(nullable = false)
    private double longitude;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CategoryEnum category;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EmotionEnum emotion;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PersonEnum personEnum;

    @Column(nullable = false)
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private IsPublicEnum is_public;
}
