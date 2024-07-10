package com.team13.mapstory.repository;

import com.team13.mapstory.entity.Emotion;
import com.team13.mapstory.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EmotionRepository extends JpaRepository<Emotion, Long> {

    Optional<Emotion> findByUser(User user);
}
