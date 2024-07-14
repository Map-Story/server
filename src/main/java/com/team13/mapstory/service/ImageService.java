package com.team13.mapstory.service;

import com.team13.mapstory.entity.Image;
import com.team13.mapstory.entity.Post;
import com.team13.mapstory.repository.ImageRepository;
import com.team13.mapstory.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ImageService {

    final private ImageRepository imageRepository;
    private final PostRepository postRepository;

    public List<String> getImages(Long id) {

        List<String> imageList = new ArrayList<>();

        Optional<Post> optionalPost = postRepository.findById(id);
        if (optionalPost.isPresent()) {
            Post post = optionalPost.get();

            List<Image> images = imageRepository.findByPost(post);
            for (Image image : images) {
                imageList.add(image.getImageUrl());
            }
        }
        return imageList;
    }
}
