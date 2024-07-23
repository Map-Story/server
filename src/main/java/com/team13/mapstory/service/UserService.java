package com.team13.mapstory.service;

import com.team13.mapstory.entity.User;
import com.team13.mapstory.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public boolean updateImage(String loginId, String image) {

        Optional<User> optionalUser = userRepository.findByLoginid(loginId);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();

            user.setProfileimage(image);
            userRepository.save(user);
            return true;
        }
        return false;
    }
}
