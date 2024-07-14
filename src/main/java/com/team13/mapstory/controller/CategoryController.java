package com.team13.mapstory.controller;

import com.team13.mapstory.dto.category.CategoryRequest;
import com.team13.mapstory.dto.category.CategoryResponse;
import com.team13.mapstory.dto.emotion.EmotionRequest;
import com.team13.mapstory.dto.emotion.EmotionResponse;
import com.team13.mapstory.jwt.JWTUtil;
import com.team13.mapstory.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/category")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;
    private final JWTUtil jwtUtil;

    // 카테고리 불러오기
    @GetMapping("/")
    public ResponseEntity<CategoryResponse> getCategory(@CookieValue(value = "Authorization") String token) {
        CategoryResponse categoryResponse = categoryService.getCategory(jwtUtil.getLoginId(token));
        if (categoryResponse != null) {
            return ResponseEntity.status(HttpStatus.OK).body(categoryResponse);
        } return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
    }

    // 카테고리 초기 설정 (계정 생성할 때 진행?)
    @PostMapping("/")
    public ResponseEntity<String> addCategory(@CookieValue(value = "Authorization") String token, @RequestBody CategoryRequest categoryRequest) {
        if (categoryService.addCategory(jwtUtil.getLoginId(token), categoryRequest)) {
            return ResponseEntity.status(HttpStatus.CREATED).body("사용자 선택 카테고리 등록 성공");
        } return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("사용자 선택 감정 등록 실패");
    }

    // 카테고리 수정하기
    @PutMapping("/")
    public ResponseEntity<String> updateCategory(@CookieValue(value = "Authorization") String token, @RequestBody CategoryRequest categoryRequest) {
        if (categoryService.updateCategory(jwtUtil.getLoginId(token), categoryRequest)) {
            return ResponseEntity.status(HttpStatus.CREATED).body("사용자 선택 카테고리 수정 성공");
        } return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("사용자 선택 카테고리 수정 실패");
    }
}
