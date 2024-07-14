package com.team13.mapstory.controller;

import com.team13.mapstory.dto.emotion.EmotionRequest;
import com.team13.mapstory.dto.emotion.EmotionResponse;
import com.team13.mapstory.jwt.JWTUtil;
import com.team13.mapstory.service.EmotionService;
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

@Tag(name = "Emotion API", description = "사용자 감정 설정과 관련된 API")
@RestController
@RequestMapping("/api/v1/emotion")
@RequiredArgsConstructor
public class EmotionController {

    private final EmotionService emotionService;
    private final JWTUtil jwtUtil;

    // 감정 불러오기
    @Operation(summary = "감정 설정 불러오기", description = "사용자가 선택한 감정 리스트를 가져옴")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "감정 불러오기에 실패했습니다.", content = @Content(schema = @Schema(type = "String"))),
            @ApiResponse(responseCode = "400", description = "감정 불러오기에 실패했습니다.", content = @Content(schema = @Schema(type = "String"))),
    })
    @GetMapping("/")
    public ResponseEntity<EmotionResponse> getEmotion(
            @Parameter(hidden = true) @CookieValue(value = "Authorization") String token) {
        EmotionResponse emotionResponse = emotionService.getEmotion(jwtUtil.getLoginId(token));
        if (emotionResponse != null) {
            return ResponseEntity.status(HttpStatus.OK).body(emotionResponse);
        } return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
    }

    // 감정 초기 설정 (계정 생성할 때 진행?)
    @Operation(summary = "감정 초기 설정", description = "사용자가 처음 로그인 할 때 감정 리스트를 설정함")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "감정 초기 설정에 성공했습니다.", content = @Content(schema = @Schema(type = "String"))),
            @ApiResponse(responseCode = "400", description = "감정 초기 설정에 실패했습니다.", content = @Content(schema = @Schema(type = "String"))),
    })
    @PostMapping("/")
    public ResponseEntity<String> addEmotion(
            @Parameter(hidden = true) @CookieValue(value = "Authorization") String token,
            @RequestBody EmotionRequest emotionRequest) {
        if (emotionService.addEmotion(jwtUtil.getLoginId(token), emotionRequest)) {
            return ResponseEntity.status(HttpStatus.CREATED).body("사용자 선택 감정 등록 성공");
        } return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("사용자 선택 감정 등록 실패");
    }

    // 감정 수정하기
    @Operation(summary = "감정 수정", description = "초기 설정이 아닌 이 후 감정 수정 (초기 설정도 이것으로 되도록 수정이 완료된 상태)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "감정 수정에 성공했습니다.", content = @Content(schema = @Schema(type = "String"))),
            @ApiResponse(responseCode = "400", description = "감정 수정에 실패했습니다.", content = @Content(schema = @Schema(type = "String"))),
    })
    @PutMapping("/")
    public ResponseEntity<String> updateEmotion(
            @Parameter(hidden = true) @CookieValue(value = "Authorization") String token,
            @RequestBody EmotionRequest emotionRequest) {
        if (emotionService.updateEmotion(jwtUtil.getLoginId(token), emotionRequest)) {
            return ResponseEntity.status(HttpStatus.CREATED).body("사용자 선택 감정 수정 성공");
        } return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("사용자 선택 감정 수정 실패");
    }
}
