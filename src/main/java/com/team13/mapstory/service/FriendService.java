package com.team13.mapstory.service;

import com.team13.mapstory.dto.CustomOAuth2User;
import com.team13.mapstory.dto.Friend.FriendResDto;
import com.team13.mapstory.entity.Friend;
import com.team13.mapstory.entity.User;
import com.team13.mapstory.exception.customException.ResourceNotFoundException;
import com.team13.mapstory.exception.customException.UserNotFoundException;
import com.team13.mapstory.repository.FriendRepository;
import com.team13.mapstory.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.team13.mapstory.entity.Friend.FriendStatus.*;

@Service
@RequiredArgsConstructor
public class FriendService {
    private final FriendRepository friendRepository;
    private final UserRepository userRepository;

    public List<FriendResDto> getAllFriends(CustomOAuth2User oAuth2User) {
//        추가한 친구 보기(status가 accepted인것)
        String loginId = oAuth2User.getLoginId();
        User loginedUser = getUserById(loginId);
        Optional<List<Friend>> friendList = friendRepository.findAllByRequestUserOrResponseUserAndStatus(loginedUser,loginedUser, ACCEPTED);
        List<Friend> friends = friendList.orElseGet(List::of);

        if(friends.size()>0) {
            return friends.stream()
                    .map(friend -> convertToDto(friend,loginedUser))
                    .collect(Collectors.toList());

        }else {
            return null;
        }
    }

    private FriendResDto convertToDto(Friend friend, User user){
        String uuid = friend.getRequestUser().getUid();
        String requester = friend.getRequestUser().getNickname();
        String responser = friend.getResponseUser().getNickname();
        String nickname = requester.equals(user.getNickname())?responser:requester;
        long id = friend.getId();
        return new FriendResDto(uuid,nickname,id);
    }
// -----TODO : 여기까지 완성
    @Transactional
    public Friend addFriend(CustomOAuth2User oAuth2User, String friendId) {
//      친구 신청
        String loginId = oAuth2User.getLoginId();

        User user = getUserById(loginId);
        User friend = userRepository.findByUid(friendId).orElseThrow(
                () -> new UserNotFoundException(friendId+" not found")
        );

//      이미 친구 관계인지 확인
        if(friendRepository.checkFriendRelationship(user,friend).isPresent()){
            throw new ResourceNotFoundException("이미 친구 추가 요청을 보냈습니다.");
        }

        Friend friendRequest = new Friend();
        friendRequest.setRequestUser(user);
        friendRequest.setResponseUser(friend);
        friendRequest.setStatus(PENDING);

        friendRepository.save(friendRequest);
        return friendRequest;
    }

    @Transactional
    public List<FriendResDto> getAllFriendReq(CustomOAuth2User oAuth2User) {
//      나(로그인한 사람)에게 온 친구 요청들
        String loginId = oAuth2User.getLoginId();;

        User user = getUserById(loginId);
        Optional<List<Friend>> friendReqList = friendRepository.findAllByResponseUser(user);
        List<Friend> friends = friendReqList.orElseGet(List::of);
        List<FriendResDto> res = friends.stream().
                map(friend->convertToDto(friend,user)
                ).collect(Collectors.toList());

        return res;
    }

    @Transactional
    public Friend acceptFriendReq(long friendId, CustomOAuth2User oAuth2User){
        String loginId = oAuth2User.getLoginId();
//      친구 요청 수락
        Friend friendReqResReq = friendRepository.findById(friendId).orElseThrow(
                ()-> new ResourceNotFoundException(friendId+" request not found")
        );
        if(friendReqResReq.getStatus().equals(PENDING)){
            if(haveActionAccess(loginId, friendReqResReq)){
                friendReqResReq.setStatus(ACCEPTED);
                friendRepository.save(friendReqResReq);
            }else{
                throw new UserNotFoundException("친구 승인 권한이 없습니다.");
            }
        }

        return friendReqResReq;
    }

    @Transactional
    public Friend rejectFriendReq(long friendId, CustomOAuth2User oAuth2User){
        String loginId = oAuth2User.getLoginId();
//      친구 요청 거절
        Friend friendReqResReq = friendRepository.findById(friendId).orElseThrow(
                ()-> new ResourceNotFoundException(friendId+" request not found")
        );

        if(friendReqResReq.getStatus().equals(PENDING)){
            if(haveActionAccess(loginId, friendReqResReq)){
                friendReqResReq.setStatus(REJECTED);
                friendRepository.save(friendReqResReq);
            }else{
                throw new UserNotFoundException("친구 거절 권한이 없습니다.");
            }
        }

        return friendReqResReq;
    }



    public Friend deleteFriend(long friendId, CustomOAuth2User oAuth2User) {
        String loginId = oAuth2User.getLoginId();

        Friend deletedFriend = friendRepository.findById(friendId).orElseThrow(
                () -> new ResourceNotFoundException(friendId+" request not found")
        );

        if(deletedFriend.getStatus().equals(ACCEPTED)){
            if(haveDeleteAccess(loginId, deletedFriend)){
                friendRepository.delete(deletedFriend);
            }else{
                throw new UserNotFoundException("친구 삭제 권한이 없습니다.");
            }
        }

        return deletedFriend;
    }

    private boolean haveDeleteAccess(String loginId, Friend friend){
        return friend.getRequestUser().getLoginid().equals(loginId) || friend.getResponseUser().getLoginid().equals(loginId);
    }

    private boolean haveActionAccess(String loginId, Friend friend){
        return friend.getResponseUser().getLoginid().equals(loginId);
    }

    private User getUserById(String loginId) {
//        Id에 해당하는 사용자 있는지 확인
        User user = userRepository.findByLoginid(loginId).orElseThrow(
                () -> new UserNotFoundException("User with Id" + loginId + " not found")
        );
        return user;
    }
}
