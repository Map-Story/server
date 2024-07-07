package com.team13.mapstory.controller;

import com.team13.mapstory.dto.post.RequestPost;
import com.team13.mapstory.dto.post.UploadPostDTO;
import com.team13.mapstory.entity.Post;
import com.team13.mapstory.entity.Test;
import com.team13.mapstory.service.PostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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

    // 게시물 조회 (전체)
    // TODO : 사용자가 동일한 지 확인하는 것 추가하기 (자기것은 비공개도 불러오고, 친구라면 친구만 허용도 불러오고, 전체공개라면 모두 불러오기)
    // TODO : 만약, 특정 개수를 정해서 불러온다면 무슨 기준을 통해 불러올지도 정해야 할듯?
    @Operation(summary = "글 목록 조회", description = "글 목록을 페이징네이션 없이 전체 불러옴")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "게시물 조회에 성공했습니다.", content = @Content(array = @ArraySchema(schema = @Schema(implementation = Post.class)))),
            @ApiResponse(responseCode = "404", description = "등록된 게시물이 없습니다."),
    })
    @GetMapping("/")
    public ResponseEntity<List<Post>> getAllPosts(@AuthenticationPrincipal CustomOAuth2User customOAuth2User) {
        List<Post> posts = postService.getAllPosts(customOAuth2User.getName());
        if (posts != null) {
            return ResponseEntity.status(HttpStatus.OK).body(posts);
        } return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
    }

    // 게시물 조회 (1건)
    // TODO : 사용자가 동일한 지 확인하는 것 추가하기
    @Operation(summary = "테스트 조회", description = "입력한아 이디의 게시물 상세 조회하기")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "게시물 조회에 성공했습니다.", content = @Content(schema = @Schema(implementation = Test.class))),
            @ApiResponse(responseCode = "404", description = "게시물 조회에 실패했습니다."),
    })
    @GetMapping("/{id}")
    public ResponseEntity<Post> getPostById(@PathVariable Long id, @AuthenticationPrincipal CustomOAuth2User customOAuth2User) {
        Post post = postService.getPostById(id, customOAuth2User.getName());
        if (post != null) {
            return ResponseEntity.status(HttpStatus.OK).body(post);
        } return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
    }

    // 이미지 정보 추출
    @Operation(summary = "이미지 정보 추출", description = "이미지를 form-data 형식으로 등록하여 사진 찍은 날짜, 위치 정보, S3에 업로드 한 주소 받아오기")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "이미지 정보 추출에 성공했습니다.", content = @Content(schema = @Schema(implementation = Test.class))),
            @ApiResponse(responseCode = "400", description = "게시물 조회에 실패했습니다."),
    })
    @PostMapping("/image")
    public ResponseEntity<RequestPost> uploadImage(@RequestParam("image") MultipartFile image) throws IOException {

        if (!image.isEmpty()) {

            RequestPost requestPost = postService.uploadImage(image);

            // 좌표 값과 이미지가 잘 들어갔는지 확인하고 보내준 주소로 그대로 전송해야 됨
            return ResponseEntity.status(HttpStatus.CREATED).body(requestPost);

        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    // 게시물 작성
    // TODO : 현재 장소 정보와 유저 정보는 제외하고 진행 중 (장소 목록 작성을 프론트엔드에서 할 지 서버에서 할 지도 고민 중...)
    @Operation(summary = "게시물 작성", description = "이미지를 직접적으로 입력하는 대신 /image 를 통해 받아온 정보를 입력함. 만약, 좌표 값이 없다면 지도에서 선택한 좌표 값을 입력하기" +
            "그 외에도 나머지 값들도 입력하기 (필수 여부는 아래에 따로 표시)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "게시물 등록에 성공했습니다.", content = @Content(schema = @Schema(type = "String"))),
            @ApiResponse(responseCode = "400", description = "게시물 등록에 실패했습니다.", content = @Content(schema = @Schema(type = "String"))),
    })
    @PostMapping("/")
    public ResponseEntity<String> uploadDetail(@RequestBody UploadPostDTO uploadPostDTO, @AuthenticationPrincipal CustomOAuth2User customOAuth2User) {
        if (postService.uploadPost(uploadPostDTO, customOAuth2User.getName())) {
            return ResponseEntity.status(HttpStatus.CREATED).body("게시물 등록 성공");
        } return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("게시물 등록 실패");
    }

    // 게시물 수정
    // TODO : 사용자가 동일한 지 확인하는 것 추가하기
    @Operation(summary = "게시물 수정", description = "사진을 변경하고 싶다면 /image를 먼저 실행해서 사진을 S3에 등록하고 메타 데이터를 추출 (수정할 때 기존 사진 삭제)" +
            "나머지는 변경한 값 DB에 반영")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "게시물 수정에 실패했습니다.", content = @Content(schema = @Schema(type = "String"))),
            @ApiResponse(responseCode = "400", description = "게시물 수정에 실패했습니다.", content = @Content(schema = @Schema(type = "String"))),
    })
    @PutMapping("/{id}")
    public ResponseEntity<String> updatePost(@PathVariable Long id, @RequestBody UploadPostDTO uploadPostDTO, @AuthenticationPrincipal CustomOAuth2User customOAuth2User) throws IOException {
        if (postService.updatePost(id, uploadPostDTO, customOAuth2User.getName())) {
            return ResponseEntity.status(HttpStatus.OK).body("게시물 수정 성공");
        } return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("게시물 수정 실패");
    }

    // 게시물 삭제
    // TODO : 사용자가 동일한 지 확인하는 것 추가하기
    @Operation(summary = "게시물 수정", description = "S3 등록된 사진 삭제 + 데이터베이스에서 항목 삭제")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "게시물 삭제에 실패했습니다.", content = @Content(schema = @Schema(type = "String"))),
            @ApiResponse(responseCode = "400", description = "게시물 삭제에 실패했습니다.", content = @Content(schema = @Schema(type = "String"))),
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deletePost(@PathVariable Long id, @AuthenticationPrincipal CustomOAuth2User customOAuth2User) throws IOException {
        if (postService.deletePost(id, customOAuth2User.getName())) {
            return ResponseEntity.status(HttpStatus.OK).body("게시물 삭제 성공");
        } return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("게시물 삭제 실패");
    }
}
