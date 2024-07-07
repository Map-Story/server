package com.team13.mapstory.repository;

import com.team13.mapstory.entity.Post;
import com.team13.mapstory.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {

    List<Post> findAllByUser(User user);
}
