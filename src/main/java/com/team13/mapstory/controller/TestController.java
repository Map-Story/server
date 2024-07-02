package com.team13.mapstory.controller;

import com.team13.mapstory.dto.test.TestDTO;
import com.team13.mapstory.entity.Test;
import com.team13.mapstory.service.TestService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "테스트", description = "테스트 Api")
@RestController
@RequestMapping("/api/v1/test")
@Slf4j
public class TestController {

    private final TestService testService;

    public TestController(TestService testService) {
        this.testService = testService;
    }

    @Operation(summary = "테스트 조회", description = "테스트 전체 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공", content = @Content(array = @ArraySchema(schema = @Schema(implementation = Test.class)))),
            @ApiResponse(responseCode = "400", description = "조회 실패"),
    })
    @GetMapping("/")
    public ResponseEntity<List<Test>> getTestUsers() {
        List<Test> test = testService.getTestUsers();
        if (test != null) return ResponseEntity.status(HttpStatus.OK).body(test);
        else return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
    }

    @Operation(summary = "테스트 조회", description = "테스트 단일 항목 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공", content = @Content(schema = @Schema(implementation = Test.class))),
            @ApiResponse(responseCode = "400", description = "조회 실패"),
    })
    @GetMapping("/{id}")
    public ResponseEntity<Test> getTestUser(@Parameter(description = "조회할 사용자의 ID", required = true) @PathVariable long id) {
        Test test = testService.getTestUesr(id);
        if (test != null) return ResponseEntity.status(HttpStatus.OK).body(test);
        else return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
    }

    @Operation(summary = "테스트 등록", description = "테스트 등록")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "등록 성공", content = @Content(schema = @Schema(type = "boolean"))),
            @ApiResponse(responseCode = "400", description = "등록 실패", content = @Content(schema = @Schema(type = "boolean"))),
    })
    @PostMapping("/")
    public ResponseEntity<Boolean> postTestUser(@RequestBody TestDTO testDTO) {
        boolean test = testService.postTestUser(testDTO);
        if (test) return ResponseEntity.status(HttpStatus.OK).body(true);
        else return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(false);
    }

    @Operation(summary = "테스트 수정", description = "테스트 수정")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "수정 성공", content = @Content(schema = @Schema(type = "boolean"))),
            @ApiResponse(responseCode = "400", description = "수정 실패", content = @Content(schema = @Schema(type = "boolean"))),
    })
    @PutMapping("/{id}")
    public ResponseEntity<Boolean> putTestUser(@Parameter(description = "수정할 사용자의 ID", required = true) @PathVariable long id, @RequestBody TestDTO testDTO) {
        boolean test = testService.putTestUser(id, testDTO);
        if (test) return ResponseEntity.status(HttpStatus.OK).body(true);
        else return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(false);
    }

    @Operation(summary = "테스트 삭제", description = "테스트 삭제")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "삭제 성공", content = @Content(schema = @Schema(type = "boolean"))),
            @ApiResponse(responseCode = "400", description = "삭제 실패", content = @Content(schema = @Schema(type = "boolean"))),
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Boolean> deleteTestUser(@Parameter(description = "삭제할 사용자의 ID", required = true) @PathVariable long id) {
        boolean test = testService.deleteTestUser(id);
        if (test) return ResponseEntity.status(HttpStatus.OK).body(true);
        else return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(false);
    }
}