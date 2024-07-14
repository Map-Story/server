package com.team13.mapstory.service;

import com.team13.mapstory.dto.category.CategoryRequest;
import com.team13.mapstory.dto.category.CategoryResponse;
import com.team13.mapstory.entity.Category;
import com.team13.mapstory.entity.User;
import com.team13.mapstory.repository.CategoryRepository;
import com.team13.mapstory.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;

    public boolean addCategory(String loginId, CategoryRequest categoryRequest) {

        boolean restaurant = categoryRequest.isRestaurant();
        boolean cafe = categoryRequest.isCafe();
        boolean date = categoryRequest.isDate();
        boolean trail = categoryRequest.isTrail();

        Optional<User> optionalUser = userRepository.findByLoginid(loginId);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();

            Category category = new Category();
            category.setUser(user);
            category.setRestaurant(restaurant);
            category.setCafe(cafe);
            category.setDate(date);
            category.setTrail(trail);

            categoryRepository.save(category);
            return true;
        }
        return false;
    }

    public boolean updateCategory(String loginId, CategoryRequest categoryRequest) {

        boolean restaurant = categoryRequest.isRestaurant();
        boolean cafe = categoryRequest.isCafe();
        boolean date = categoryRequest.isDate();
        boolean trail = categoryRequest.isTrail();

        Optional<User> optionalUser = userRepository.findByLoginid(loginId);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();

            Optional<Category> optionalCategory = categoryRepository.findByUser(user);
            if (optionalCategory.isPresent()) {
                Category category = optionalCategory.get();

                category.setRestaurant(restaurant);
                category.setCafe(cafe);
                category.setDate(date);
                category.setTrail(trail);

                categoryRepository.save(category);
                return true;
            } else {
                Category category = new Category();
                category.setUser(user);
                category.setRestaurant(restaurant);
                category.setCafe(cafe);
                category.setDate(date);
                category.setTrail(trail);

                categoryRepository.save(category);
                return true;
            }
        }
        return false;
    }

    public CategoryResponse getCategory(String loginId) {
        Optional<User> optionalUser = userRepository.findByLoginid(loginId);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();

            Optional<Category> optionalCategory = categoryRepository.findByUser(user);
            if (optionalCategory.isPresent()) {
                Category category = optionalCategory.get();

                CategoryResponse categoryResponse = new CategoryResponse();
                categoryResponse.setRestaurant(category.isRestaurant());
                categoryResponse.setCafe(category.isCafe());
                categoryResponse.setDate(category.isDate());
                categoryResponse.setTrail(category.isTrail());

                return categoryResponse;
            }
        }
        return null;
    }
}
