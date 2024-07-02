package com.team13.mapstory.service;

import com.team13.mapstory.dto.test.TestDTO;
import com.team13.mapstory.entity.Test;
import com.team13.mapstory.repository.TestRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class TestService {

    private final TestRepository testRepository;

    public TestService(TestRepository testRepository) {
        this.testRepository = testRepository;
    }

    public List<Test> getTestUsers() {
        return testRepository.findAll();
    }


    public Test getTestUesr(long id) {

        Optional<Test> optionalTest = testRepository.findById(id);

        return optionalTest.orElse(null);
    }

    public boolean postTestUser(TestDTO testDTO) {
        try {
            String name = testDTO.getName();

            Test test = new Test();
            test.setName(name);
            testRepository.save(test);

            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean putTestUser(long id, TestDTO testDTO) {

        String name = testDTO.getName();

        Optional<Test> optionalTest = testRepository.findById(id);

        if (optionalTest.isPresent()) {
            Test test = optionalTest.get();
            test.setName(name);
            testRepository.save(test);
            return true;
        }
        return false;
    }

    public boolean deleteTestUser(long id) {

        Optional<Test> optionalTest = testRepository.findById(id);

        if (optionalTest.isPresent()) {

            testRepository.deleteById(id);
            return true;
        }
        return false;
    }
}