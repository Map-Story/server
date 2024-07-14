package com.team13.mapstory.repository;

import com.team13.mapstory.entity.Image;
import com.team13.mapstory.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ImageRepository extends JpaRepository<Image, Long> {

    List<Image> findByPost(Post post);
    Optional<Image> findByPostAndImageUrl(Post post, String imageUrl);
}
