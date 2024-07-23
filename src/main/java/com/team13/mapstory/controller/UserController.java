package com.team13.mapstory.controller;

import com.team13.mapstory.dto.user.UserImageDTO;
import com.team13.mapstory.jwt.JWTUtil;
import com.team13.mapstory.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "User API", description = "사용자 설정과 관련된 API")
@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final JWTUtil jwtUtil;

    @Operation(summary = "프로필 이미지 변경하기", description = "사용자가 원하는 이미지로 변경하기")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "프로필 이미지 변경에 성공했습니다.", content = @Content(schema = @Schema(type = "String"))),
            @ApiResponse(responseCode = "400", description = "프로필 이미지 변경에 실패했습니다.", content = @Content(schema = @Schema(type = "String"))),
    })
    @PutMapping("/image")
    public ResponseEntity<String> updateProfileImage(
            @Parameter(hidden = true) @CookieValue(value = "Authorization") String token,
            @RequestBody UserImageDTO userImageDTO) {
        if (userService.updateImage(jwtUtil.getLoginId(token), userImageDTO.getImage())) {
            return ResponseEntity.status(HttpStatus.OK).body("이미지 수정 성공");
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("이미지 수정 실패");
    }
}
