package com.team13.mapstory.repository;

import com.team13.mapstory.entity.Image;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ImageRepository extends JpaRepository<Image, Long> {
}
