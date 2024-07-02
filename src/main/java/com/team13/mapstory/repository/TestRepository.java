package com.team13.mapstory.repository;

import com.team13.mapstory.entity.Test;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TestRepository extends JpaRepository<Test, Long> {

    List<Test> findAll();
    Optional<Test> findById(long id);
}