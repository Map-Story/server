package com.team13.mapstory.dto.category;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
public class CategoryRequest {

    private boolean restaurant;
    private boolean cafe;
    private boolean date;
    private boolean trail;
}
