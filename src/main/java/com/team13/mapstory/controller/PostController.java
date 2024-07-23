package com.team13.mapstory.controller;

import com.team13.mapstory.dto.post.*;
import com.team13.mapstory.jwt.JWTUtil;
import com.team13.mapstory.service.PostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Tag(name = "Post API", description = "게시물과 관련된 API")
@RestController
@RequestMapping("/api/v1/post")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;
    private final JWTUtil jwtUtil;

    @Operation(summary = "글 목록 조회", description = "글 목록을 페이징네이션 없이 전체 불러옴")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "게시물 조회에 성공했습니다.", content = @Content(array = @ArraySchema(schema = @Schema(implementation = GetsPostResponse.class)))),
            @ApiResponse(responseCode = "404", description = "등록된 게시물이 없습니다."),
    })
    @GetMapping("/")
    public ResponseEntity<List<GetsPostResponse>> getAllPosts(
            @Parameter(hidden = true) @CookieValue(value = "Authorization") String token) {
        List<GetsPostResponse> getsPostResponse = postService.getAllPosts(jwtUtil.getLoginId(token));
        if (getsPostResponse != null) {
            return ResponseEntity.status(HttpStatus.OK).body(getsPostResponse);
        } return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
    }

    // 게시물 조회 (1건)
    @Operation(summary = "게시물 조회", description = "입력한 아이디의 게시물 상세 조회하기")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "게시물 조회에 성공했습니다.", content = @Content(schema = @Schema(implementation = GetPostResponse.class))),
            @ApiResponse(responseCode = "404", description = "게시물 조회에 실패했습니다."),
    })
    @GetMapping("/{id}")
    public ResponseEntity<GetPostResponse> getPostById(
            @Parameter(description = "Post ID", required = true) @PathVariable Long id,
            @Parameter(hidden = true) @CookieValue(value = "Authorization") String token) {
        GetPostResponse getPostResponse = postService.getPostById(id, jwtUtil.getLoginId(token));
        if (getPostResponse != null) {
            return ResponseEntity.status(HttpStatus.OK).body(getPostResponse);
        } return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
    }

    // 대표 이미지 정보 추출
    @Operation(summary = "이미지 정보 추출", description = "이미지를 form-data 형식으로 등록하여 사진 찍은 날짜, 위치 정보, S3에 업로드 한 Url 받아오기")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "이미지 정보 추출에 성공했습니다.", content = @Content(schema = @Schema(implementation = PostResponse.class))),
            @ApiResponse(responseCode = "400", description = "게시물 조회에 실패했습니다."),
    })
    @PostMapping(value = "/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PostResponse> uploadImage(
            @Parameter(description = "대표 이미지", required = true) @RequestParam("image") MultipartFile image) throws IOException {
        if (!image.isEmpty()) {
            PostResponse postResponse = postService.uploadImage(image);
            return ResponseEntity.status(HttpStatus.CREATED).body(postResponse);
        } return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
    }

    // 게시물 작성
    @Operation(summary = "게시물 작성", description = "이미지를 직접적으로 입력하는 대신 /image 를 통해 받아온 정보를 입력함. 만약, 좌표 값이 없다면 지도에서 선택한 좌표 값을 입력하기" +
            "그 외에도 나머지 값들도 입력하기 (필수 여부는 아래에 따로 표시), 이미지의 용량은 1개 최대 10MB, 전체 최대 50MB 까지 등록 가능 (최대 메인 1개 + 나머지 9개 = 10개")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "게시물 등록에 성공했습니다.", content = @Content(schema = @Schema(type = "String"))),
            @ApiResponse(responseCode = "400", description = "게시물 등록에 실패했습니다.", content = @Content(schema = @Schema(type = "String"))),
    })
    @PostMapping(value = "/", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> uploadPost(
            @ModelAttribute UploadPostDTO uploadPostDTO,
            @Parameter(description = "나머지 이미지") @RequestPart(value = "images", required = false) List<MultipartFile> images,
            @Parameter(hidden = true) @CookieValue(value = "Authorization") String token) throws IOException {

        if (postService.uploadPost(uploadPostDTO, images, jwtUtil.getLoginId(token))) {
            return ResponseEntity.status(HttpStatus.CREATED).body("게시물 등록 성공");
        } return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("게시물 등록 실패");
    }

    // 이미지들 링크 추출
    @Operation(summary = "이미지들 링크 추출", description = "이미지들을 S3에 저장해서 링크를 불러옴 -> 이 링크를 바탕으로 해서 게시물 수정 시 사용 (파일 추가 등록 시)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "게시물 등록에 성공했습니다.", content = @Content(schema = @Schema(type = "String"))),
            @ApiResponse(responseCode = "400", description = "게시물 등록에 실패했습니다.", content = @Content(schema = @Schema(type = "String"))),
    })
    @PostMapping(value = "/images", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<String>> uploadImages(
            @Parameter(description = "나머지 이미지") @RequestPart(value = "images", required = false) List<MultipartFile> images) throws IOException {
        List<String> imagesUrl = postService.uploadImageS3(images);
        if (imagesUrl != null) {
            return ResponseEntity.status(HttpStatus.CREATED).body(imagesUrl);
        } return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
    }

    // 게시물 수정
    @Operation(summary = "게시물 수정", description = "메인 사진을 변경하고 싶으면 /image를 실행하고, 메인 사진이 아닌 다른 사진을 변경하고 싶다면 /images를 실행" +
            "게시물 수정하기 전에 /GET{id}를 통해 게시물의 세부 정보를 받아와서 그 정보를 바탕으로 수정하기!" +
            " 메인 사진이 아닌 나머지 사진 같은 경우에는 기존 사진의 목록에서 ADD + DELETE를 이용해서 기존의 상태에서 무엇이 바뀌었는지를 보내주어야 함")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "게시물 수정에 실패했습니다.", content = @Content(schema = @Schema(type = "String"))),
            @ApiResponse(responseCode = "400", description = "게시물 수정에 실패했습니다.", content = @Content(schema = @Schema(type = "String"))),
    })
    @PutMapping("/{id}")
    public ResponseEntity<String> updatePost(
            @Parameter(description = "Post Id", required = true) @PathVariable Long id,
            @RequestBody UpdatePostRequest updatePostRequest,
            @Parameter(hidden = true) @CookieValue(value = "Authorization") String token) throws IOException {
        String result = postService.updatePost(id, updatePostRequest, jwtUtil.getLoginId(token));
        if (result.equals("게시물 수정 성공")) {
            return ResponseEntity.status(HttpStatus.OK).body(result);
        } return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
    }

    // 게시물 삭제
    @Operation(summary = "게시물 삭제", description = "S3 등록된 사진 삭제 및 데이터베이스에서 항목 삭제 + 게시물 삭제")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "게시물 삭제에 실패했습니다.", content = @Content(schema = @Schema(type = "String"))),
            @ApiResponse(responseCode = "400", description = "게시물 삭제에 실패했습니다.", content = @Content(schema = @Schema(type = "String"))),
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deletePost(
            @Parameter(description = "Post Id", required = true) @PathVariable Long id,
            @Parameter(hidden = true) @CookieValue(value = "Authorization") String token) throws IOException {
        String result = postService.deletePost(id, jwtUtil.getLoginId(token));
        if (result.equals("게시물 삭제 성공")) {
            return ResponseEntity.status(HttpStatus.OK).body(result);
        } return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
    }
}
