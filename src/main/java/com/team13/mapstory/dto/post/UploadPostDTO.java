package com.team13.mapstory.dto.post;

import com.team13.mapstory.entity.enums.CategoryEnum;
import com.team13.mapstory.entity.enums.EmotionEnum;
import com.team13.mapstory.entity.enums.IsPublicEnum;
import com.team13.mapstory.entity.enums.PersonEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class UploadPostDTO {

    @Schema(description = "S3에 저장된 대표 이미지 URL", example = "https://map-story-team13.s3.ap-northeast-2.amazonaws.com/d5aaba88-4690-42cc-8f92-ec200253ad0b_aws.png")
    private String imageUrl;

    @Schema(description = "위도", example = "37.120504389769825")
    private double latitude;

    @Schema(description = "경도", example = "127.31572199992323")
    private double longitude;

    @Schema(description = "사진 찍은 시간 [예제에는 2024-07-01T01:02:03Z처럼 나온다면 2024-07-01T01:02:03처럼 Z빼고 사용하기]", example = "2024-07-01T01:02:03")
    private LocalDateTime uploadTime;

    @Schema(description = "간단한 내용", example = "오늘하루는 완벽했어요")
    private String content;

    @Schema(description = "음식점, 카페, 관광지 등 카테고리", example = "CAFE")
    private CategoryEnum category;

    @Schema(description = "기분을 이모티콘으로 표현", example = "SAD")
    private EmotionEnum emotion;

    @Schema(description = "같이 간 사람", example = "PET")
    private PersonEnum person;

    @Schema(description = "공개 여부 (비공개, 친구 공개, 모두 공개)", example = "PUBLIC")
    private IsPublicEnum isPublic;
}
