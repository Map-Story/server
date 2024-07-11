package com.team13.mapstory.dto.location;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddressDTO {

    @Schema(description = "도로명 주소", example = "서울특별시 중구 신당동 다산로34길 13 대찬빌라")
    private String roadAddress;

    @Schema(description = "지번 주소", example = "서울특별시 중구 신당동 290-49")
    private String address;

    @Schema(description = "건물 이름", example = "대찬빌라")
    private String building;
}
