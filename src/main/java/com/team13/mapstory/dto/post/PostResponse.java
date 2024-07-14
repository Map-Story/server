package com.team13.mapstory.dto.post;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class PostResponse {

    @Schema(description = "위도", example = "37.120504389769825")
    private double latitude;

    @Schema(description = "경도", example = "127.31572199992323")
    private double longitude;

    @Schema(description = "사진 찍은 시간 (없을 시에 업로드 시간)", example = "2024-07-04T12:33:27")
    private LocalDateTime uploadTime;

    @Schema(description = "S3에 저장된 이미지 URL", example = "https://map-story-team13.s3.ap-northeast-2.amazonaws.com/d5aaba88-4690-42cc-8f92-ec200253ad0b_aws.png")
    private String imageUrl;

    @Schema(description = "도로명 주소", example = "서울특별시 중구 신당동 다산로34길 13 대찬빌라")
    private String roadAddress;

    @Schema(description = "지번 주소", example = "서울특별시 중구 신당동 290-49")
    private String address;

    @Schema(description = "건물 이름", example = "대찬빌라")
    private String building;
}

