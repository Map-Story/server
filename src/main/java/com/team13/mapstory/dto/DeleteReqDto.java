package com.team13.mapstory.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DeleteReqDto {
    private long friendId;
    private String friendNickName;
}
