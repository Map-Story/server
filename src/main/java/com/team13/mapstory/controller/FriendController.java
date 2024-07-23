package com.team13.mapstory.controller;

import com.team13.mapstory.dto.CustomOAuth2User;
import com.team13.mapstory.dto.DeleteReqDto;
import com.team13.mapstory.dto.Friend.FriendResDto;
import com.team13.mapstory.entity.Friend;
import com.team13.mapstory.service.FriendService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Tag(name = "Share API", description = "친구관리와 관련된 API")
@RestController
@RequestMapping("/api/v1/friend")
@RequiredArgsConstructor
public class FriendController {
    private final FriendService friendService;
//    참고사항 jwt에는 이름(닉네임), 역할, 로그인 아이디가 들어가 있음
//    친구 목록 보기, 친구 추가 수락 요청, 친구 추가 승인, 친구 추가 거절, 친구 삭제, 나에게 온 친구 요청 보기

//  친구 목록 보기
    @Operation(summary = "친구 목록 가져오기", description = "친구 목록을 페이지네이션 없이 전체 불러옴")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "조회 성공, 추가한 친구 없음",content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(type="object",example = "")
            )),
            @ApiResponse(responseCode = "200", description = "친구 목록 조회 성공",content = @Content(array =@ArraySchema(schema = @Schema(implementation = FriendResDto.class)))),
            @ApiResponse(responseCode = "400", description = "요청이 잘못됐을때",content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(type="object",example = "")
            ))
    })
    @GetMapping()
    public ResponseEntity<List<FriendResDto>> getFriendList(@AuthenticationPrincipal CustomOAuth2User oAuth2User) {
           List<FriendResDto> friendReqRes = friendService.getAllFriends(oAuth2User);
           if(friendReqRes == null){
               return ResponseEntity.noContent().build();
           }else{
               return ResponseEntity.ok(friendReqRes);
        }
    }
    //    친구 추가 수락 요청
    @Operation(summary = "친구 요청 보내기", description = "친구 추가하고자 하는 상대의 uuid를 통해 친구 추가 요청")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "친구 요청 성공",content = @Content(mediaType = "text/plain")),
            @ApiResponse(responseCode = "400", description = "요청이 잘못됐을때")
    })
    @PostMapping("/{friendUuid}")
    public ResponseEntity<String> requestToFriend(@PathVariable String friendUuid, @AuthenticationPrincipal CustomOAuth2User oAuth2User){

        Friend friendRequest = friendService.addFriend(oAuth2User, friendUuid);
        return ResponseEntity.ok(friendRequest.getResponseUser().getNickname()+"에게 친구 요청을 보냈습니다.");
    }

    //    친구 추가 수락 승인
    @Operation(summary = "친구 추가 수락 승인", description = "친구 관계 ID를 통해 친구 추가 수락")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "친구 요청 수락 성공"),
            @ApiResponse(responseCode = "400", description = "요청이 잘못됐을때")
    }
    )
    @PostMapping("/accept/{friendReqId}")
    public ResponseEntity<String> acceptFriend(@PathVariable long friendReqId, @AuthenticationPrincipal CustomOAuth2User oAuth2User){
        Friend acceptedFriendReqResReq = friendService.acceptFriendReq(friendReqId,oAuth2User);

        return ResponseEntity.ok(acceptedFriendReqResReq.getRequestUser().getNickname()+"의 친구 요청을 수락하셨습니다.");
    }

    //    친구 추가 수락 거절
    @Operation(summary = "친구 추가 수락 거절", description = "친구 관계 ID를 통해 친구 추가 거절")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "친구 요청 거절"),
            @ApiResponse(responseCode = "400", description = "요청이 잘못됐을때")
    })
    @PostMapping("/reject/{friendReqId}")
    public ResponseEntity<String> rejectFriend(@PathVariable long friendReqId, @AuthenticationPrincipal CustomOAuth2User oAuth2User){
        Friend rejectedFriendReqResReq = friendService.rejectFriendReq(friendReqId, oAuth2User);

        return ResponseEntity.ok(rejectedFriendReqResReq.getRequestUser().getNickname()+"의 친구수락 요청을 거절하셨습니다.");
    }


    //    친구 삭제
    @Operation(summary = "등록된 친구 삭제", description = "등록된 친구 관계 ID를 통해 친구 삭제")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",description = "친구 삭제 성공"),
            @ApiResponse(responseCode = "400", description = "요청이 잘못됐을때")
    })
    @DeleteMapping()
    public ResponseEntity<String> deleteFriend(
            @AuthenticationPrincipal CustomOAuth2User oAuth2User,
            @RequestBody DeleteReqDto deleteReqDto
    ){
        long friendId = deleteReqDto.getFriendId();
        friendService.deleteFriend(friendId,oAuth2User);
        return ResponseEntity.ok(deleteReqDto.getFriendNickName()+"을 친구목록에서 삭제하였습니다.");
    }

    //    나에게 온 친구 요청 보기
    @Operation(summary = "나에게 온 친구 요청 보기", description = "로그인한 사용자에게 온 친구 요청 보기")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",description = "친구 요청 조회 성공"),
            @ApiResponse(responseCode = "204",description = "조회 성공, 친구 요청 없음"),
            @ApiResponse(responseCode = "400", description = "요청이 잘못됐을때")
    })
    @GetMapping("/requests")
    public ResponseEntity<List<FriendResDto>> getFriendRequests(@AuthenticationPrincipal CustomOAuth2User oAuth2User){
        List<FriendResDto> friendReqList = friendService.getAllFriendReq(oAuth2User);

        if(friendReqList.size()>0){
            return ResponseEntity.ok(friendReqList);
        }else {
            return ResponseEntity.noContent().build();
        }
    }


}
