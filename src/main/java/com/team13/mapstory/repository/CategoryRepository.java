package com.team13.mapstory.repository;

import com.team13.mapstory.entity.Category;
import com.team13.mapstory.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    Optional<Category> findByUser(User user);
}
