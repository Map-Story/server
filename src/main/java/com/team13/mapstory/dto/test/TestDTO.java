package com.team13.mapstory.dto.test;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
public class TestDTO {

    @Schema(description = "이름", defaultValue = "홍길동")
    private String name;
}