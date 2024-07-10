package com.team13.mapstory.repository;

import com.team13.mapstory.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

    User findByLoginid(String loginId);
}
