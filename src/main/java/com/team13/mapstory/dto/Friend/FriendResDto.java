package com.team13.mapstory.dto.Friend;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FriendResDto {
    @Schema(description = "친구 테이블의 id", example = "1")
    private long id;
    @Schema(description = "친구코드", example = "550e8400-e29b-41d4-a716-446655440000")
    private String uuid;
    @Schema(description = "닉네임", example = "홍길동")
    private String nickName;

    public FriendResDto(String uuid, String nickname, long id) {
        this.uuid = uuid;
        this.nickName = nickname;
        this.id = id;
    }
}
