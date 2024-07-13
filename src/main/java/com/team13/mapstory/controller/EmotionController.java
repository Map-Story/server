package com.team13.mapstory.controller;

import com.team13.mapstory.dto.emotion.EmotionRequest;
import com.team13.mapstory.dto.emotion.EmotionResponse;
import com.team13.mapstory.jwt.JWTUtil;
import com.team13.mapstory.service.EmotionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/emotion")
@RequiredArgsConstructor
public class EmotionController {

    private final EmotionService emotionService;
    private final JWTUtil jwtUtil;

    // 감정 불러오기
    @GetMapping("/")
    public ResponseEntity<EmotionResponse> getEmotion(@CookieValue(value = "Authorization") String token) {
        EmotionResponse emotionResponse = emotionService.getEmotion(jwtUtil.getLoginId(token));
        if (emotionResponse != null) {
            return ResponseEntity.status(HttpStatus.OK).body(emotionResponse);
        } return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
    }

    // 감정 초기 설정 (계정 생성할 때 진행?)
    @PostMapping("/")
    public ResponseEntity<String> addEmotion(@CookieValue(value = "Authorization") String token, @RequestBody EmotionRequest emotionRequest) {
        if (emotionService.addEmotion(jwtUtil.getLoginId(token), emotionRequest)) {
            return ResponseEntity.status(HttpStatus.CREATED).body("사용자 선택 감정 등록 성공");
        } return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("사용자 선택 감정 등록 실패");
    }

    // 감정 수정하기
    @PutMapping("/")
    public ResponseEntity<String> updateEmotion(@CookieValue(value = "Authorization") String token, @RequestBody EmotionRequest emotionRequest) {
        if (emotionService.updateEmotion(jwtUtil.getLoginId(token), emotionRequest)) {
            return ResponseEntity.status(HttpStatus.CREATED).body("사용자 선택 감정 수정 성공");
        } return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("사용자 선택 감정 수정 실패");
    }
}
