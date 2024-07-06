package com.team13.mapstory.service;

import com.team13.mapstory.dto.*;
import com.team13.mapstory.entity.UserEntity;
import com.team13.mapstory.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        OAuth2User oAuth2User = super.loadUser(userRequest);

        System.out.println(oAuth2User);

        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        OAuth2Response oAuth2Response = null;
        if (registrationId.equals("kakao")) {

            oAuth2Response = new KakaoResponse(oAuth2User.getAttributes());
        }
        else if (registrationId.equals("google")) {

            oAuth2Response = new GoogleResponse(oAuth2User.getAttributes());
        }
        else {

            return null;
        }

        //리소스 서버에서 발급 받은 정보로 사용자를 특정할 아이디값을 만듬
        String loginId = oAuth2Response.getProviderId();
        UserEntity existData = userRepository.findByLoginid(loginId);


        if (existData == null) {

            UserEntity userEntity = new UserEntity();
            userEntity.setLogintype(oAuth2Response.getProvider());
            userEntity.setLoginid(loginId);
            userEntity.setNickname(oAuth2Response.getNickName());
            userEntity.setProfileimage(oAuth2Response.getProfileImage());

            userRepository.save(userEntity);

            UserDTO userDTO = new UserDTO();
            userDTO.setNickname(oAuth2Response.getNickName());
            userDTO.setProfileimage(oAuth2Response.getProfileImage());

            return new CustomOAuth2User(userDTO);
        }
        else {

            existData.setProfileimage(oAuth2Response.getProfileImage());
            existData.setNickname(oAuth2Response.getNickName());

            userRepository.save(existData);

            UserDTO userDTO = new UserDTO();
            userDTO.setNickname(oAuth2Response.getNickName());
            userDTO.setProfileimage(oAuth2Response.getProfileImage());

            return new CustomOAuth2User(userDTO);
        }
    }
}
