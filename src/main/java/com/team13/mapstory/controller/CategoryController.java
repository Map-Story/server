package com.team13.mapstory.controller;

import com.team13.mapstory.dto.category.CategoryRequest;
import com.team13.mapstory.dto.category.CategoryResponse;
import com.team13.mapstory.jwt.JWTUtil;
import com.team13.mapstory.service.CategoryService;
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

@Tag(name = "Category API", description = "사용자 카테고리 설정과 관련된 API")
@RestController
@RequestMapping("/api/v1/category")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;
    private final JWTUtil jwtUtil;

    // 카테고리 불러오기
    @Operation(summary = "카테고리 설정 불러오기", description = "사용자가 선택한 카테고리 리스트를 가져옴")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "카테고리 불러오기에 실패했습니다.", content = @Content(schema = @Schema(type = "String"))),
            @ApiResponse(responseCode = "400", description = "카테고리 불러오기에 실패했습니다.", content = @Content(schema = @Schema(type = "String"))),
    })
    @GetMapping("/")
    public ResponseEntity<CategoryResponse> getCategory(
            @Parameter(hidden = true) @CookieValue(value = "Authorization") String token) {
        CategoryResponse categoryResponse = categoryService.getCategory(jwtUtil.getLoginId(token));
        if (categoryResponse != null) {
            return ResponseEntity.status(HttpStatus.OK).body(categoryResponse);
        } return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
    }

    // 카테고리 초기 설정 (계정 생성할 때 진행?)
    @Operation(summary = "카테고리 초기 설정", description = "사용자가 처음 로그인 할 때 카테고리 리스트를 설정함")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "카테고리 초기 설정에 성공했습니다.", content = @Content(schema = @Schema(type = "String"))),
            @ApiResponse(responseCode = "400", description = "카테고리 초기 설정에 실패했습니다.", content = @Content(schema = @Schema(type = "String"))),
    })
    @PostMapping("/")
    public ResponseEntity<String> addCategory(
            @Parameter(hidden = true) @CookieValue(value = "Authorization") String token,
            @RequestBody CategoryRequest categoryRequest) {
        if (categoryService.addCategory(jwtUtil.getLoginId(token), categoryRequest)) {
            return ResponseEntity.status(HttpStatus.CREATED).body("사용자 선택 카테고리 등록 성공");
        } return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("사용자 선택 카테고리 등록 실패");
    }

    // 카테고리 수정하기
    @Operation(summary = "카테고리 수정", description = "초기 설정이 아닌 이 후 카테고리 수정 (초기 설정도 이것으로 되도록 수정이 완료된 상태)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "카테고리 수정에 성공했습니다.", content = @Content(schema = @Schema(type = "String"))),
            @ApiResponse(responseCode = "400", description = "카테고리 수정에 실패했습니다.", content = @Content(schema = @Schema(type = "String"))),
    })
    @PutMapping("/")
    public ResponseEntity<String> updateCategory(
            @Parameter(hidden = true) @CookieValue(value = "Authorization") String token,
            @RequestBody CategoryRequest categoryRequest) {
        if (categoryService.updateCategory(jwtUtil.getLoginId(token), categoryRequest)) {
            return ResponseEntity.status(HttpStatus.CREATED).body("사용자 선택 카테고리 수정 성공");
        } return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("사용자 선택 카테고리 수정 실패");
    }
}
