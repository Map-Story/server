package com.team13.mapstory.dto.post;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class UploadPostDTO {

    @Schema(description = "S3에 저장된 이미지 URL", example = "https://map-story-team13.s3.ap-northeast-2.amazonaws.com/d5aaba88-4690-42cc-8f92-ec200253ad0b_aws.png")
    private String imageUrl;

    @Schema(description = "위도", example = "37.120504389769825")
    private double latitude;

    @Schema(description = "경도", example = "127.31572199992323")
    private double longitude;

    @Schema(description = "사진 찍은 시간 (없을 시에 업로드 시간)", example = "2024-07-04T12:33:27")
    private LocalDateTime uploadTime;

    @Schema(description = "간단하게 제시되는 내용", example = "오늘하루는 완벽했어요")
    private String content;

    @Schema(description = "음식점, 카페, 관광지 등 카테고리", example = "음식점")
    private String category;

    @Schema(description = "기분을 이모티콘으로 표현 (만약 이모티콘이 없을 시에 S3에서 추가적으로 다운로드 하기)", example = "기분 좋음")
    private String emotion;

    @Schema(description = "같이 간 사람", example = "연인")
    private String person;

    @Schema(description = "날씨", example = "맑음")
    private String weather;

    @Schema(description = "공개 여부 (비공개, 친구 공개, 모두 공개)", example = "친구 공개")
    private String isPublic;
}
