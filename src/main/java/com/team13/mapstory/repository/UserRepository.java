package com.team13.mapstory.repository;

import com.team13.mapstory.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    User findByLoginid(String loginId);

    Optional<User> findByNickname(String nickname);
}
