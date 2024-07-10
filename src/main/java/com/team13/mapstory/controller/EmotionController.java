package com.team13.mapstory.controller;

import com.team13.mapstory.dto.CustomOAuth2User;
import com.team13.mapstory.dto.emotion.EmotionRequest;
import com.team13.mapstory.dto.emotion.EmotionResponse;
import com.team13.mapstory.service.EmotionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/emotion")
@RequiredArgsConstructor
public class EmotionController {

    private final EmotionService emotionService;

    // 감정 불러오기
    @GetMapping("/")
    public ResponseEntity<EmotionResponse> getEmotion(@AuthenticationPrincipal CustomOAuth2User customOAuth2User) {
        EmotionResponse emotionResponse = emotionService.getEmotion(customOAuth2User.getLoginId());
        if (emotionResponse != null) {
            return ResponseEntity.status(HttpStatus.OK).body(emotionResponse);
        } return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
    }

    // 감정 초기 설정
    @PostMapping("/")
    public ResponseEntity<String> addEmotion(@AuthenticationPrincipal CustomOAuth2User customOAuth2User, @RequestBody EmotionRequest emotionRequest) {
        if (emotionService.addEmotion(customOAuth2User.getLoginId(), emotionRequest)) {
            return ResponseEntity.status(HttpStatus.CREATED).body("사용자 선택 감정 등록 성공");
        } return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("사용자 선택 감정 등록 실패");
    }

    // 감정 수정하기
    @PutMapping("/")
    public ResponseEntity<String> updateEmotion(@AuthenticationPrincipal CustomOAuth2User customOAuth2User, @RequestBody EmotionRequest emotionRequest) {
        if (emotionService.updateEmotion(customOAuth2User.getLoginId(), emotionRequest)) {
            return ResponseEntity.status(HttpStatus.CREATED).body("사용자 선택 감정 수정 성공");
        } return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("사용자 선택 감정 수정 실패");
    }
}
