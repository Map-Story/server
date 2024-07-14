package com.team13.mapstory.service;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.drew.imaging.ImageMetadataReader;
import com.drew.lang.GeoLocation;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifSubIFDDirectory;
import com.drew.metadata.exif.GpsDirectory;
import com.drew.metadata.png.PngDirectory;
import com.team13.mapstory.dto.location.AddressDTO;
import com.team13.mapstory.dto.post.*;
import com.team13.mapstory.entity.*;
import com.team13.mapstory.entity.enums.CategoryEnum;
import com.team13.mapstory.entity.enums.EmotionEnum;
import com.team13.mapstory.entity.enums.IsPublicEnum;
import com.team13.mapstory.entity.enums.PersonEnum;
import com.team13.mapstory.entity.only.CategoryOnly;
import com.team13.mapstory.entity.only.EmotionOnly;
import com.team13.mapstory.repository.*;
import lombok.RequiredArgsConstructor;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;

@Service
@RequiredArgsConstructor
public class PostService {

    private final ImageRepository imageRepository;
    private final ImageService imageService;
    private final EmotionRepository emotionRepository;
    private final CategoryRepository categoryRepository;
    @Value("${naver-map-api.client-id}")
    private String naverMapClientId;

    @Value("${naver-map-api.client-secret}")
    private String naverMapClientSecret;

    private final AmazonS3Client s3Client;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    @Value("${aws.default.image.post}")
    private String defaultPostImage;

    public List<GetsPostResponse> getAllPosts(String loginId) {

        List<GetsPostResponse> getsPostResponse = new ArrayList<>();

        Optional<User> optionalUser = userRepository.findByLoginid(loginId);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();

            List<Post> posts = postRepository.findAllByUser(user);
            for (Post post : posts) {

                GetsPostResponse getPostDTO = new GetsPostResponse();

                getPostDTO.setId(post.getId());
                getPostDTO.setImage(post.getImage());
                getPostDTO.setUploadTime(post.getUpload_time());
                getPostDTO.setLatitude(post.getLatitude());
                getPostDTO.setLongitude(post.getLongitude());
                getPostDTO.setUserId(post.getUser().getId());
                getPostDTO.setEmotion(post.getEmotion());
                getPostDTO.setIsPublic(post.getIs_public());

                getsPostResponse.add(getPostDTO);
            }
            return getsPostResponse;
        }
        return null;
    }

    public GetPostResponse getPostById(Long id, String loginId) {

        GetPostResponse getPostResponse = new GetPostResponse();

        Optional<Post> optionalPost = postRepository.findById(id);
        if (optionalPost.isPresent()) {
            Post post = optionalPost.get();

            Optional<User> optionalUser = userRepository.findByLoginid(loginId);
            if (optionalUser.isPresent()) {

                User user = optionalUser.get();

                Optional<Emotion> optionalEmotion = emotionRepository.findByUser(user);
                if (optionalEmotion.isEmpty()) {
                    return null;
                }
                Emotion emotion = optionalEmotion.get();

                Optional<Category> optionalCategory = categoryRepository.findByUser(user);
                if (optionalCategory.isEmpty()) {
                    return null;
                }
                Category category = optionalCategory.get();

                List<String> images = imageService.getImages(id);

                getPostResponse.setId(id);
                getPostResponse.setImage(post.getImage());
                getPostResponse.setUploadTime(post.getUpload_time());
                getPostResponse.setLatitude(post.getLatitude());
                getPostResponse.setLongitude(post.getLongitude());
                getPostResponse.setCategory(post.getCategory());
                getPostResponse.setUserId(post.getUser().getId());
                getPostResponse.setEmotion(post.getEmotion());
                getPostResponse.setPerson(post.getPerson());
                getPostResponse.setContent(post.getContent());
                getPostResponse.setIsPublic(post.getIs_public());
                getPostResponse.setImages(images);

                CategoryOnly categoryOnly = new CategoryOnly();
                categoryOnly.setRestaurant(category.isRestaurant());
                categoryOnly.setCafe(category.isCafe());
                categoryOnly.setDate(categoryOnly.isDate());
                categoryOnly.setTrail(categoryOnly.isTrail());
                getPostResponse.setCategoryOnly(categoryOnly);

                EmotionOnly emotionOnly = new EmotionOnly();
                emotionOnly.setHappy(emotion.isHappy());
                emotionOnly.setSad(emotion.isSad());
                emotionOnly.setDepressed(emotion.isDepressed());
                emotionOnly.setStress(emotion.isStress());
                emotionOnly.setAngry(emotion.isAngry());
                emotionOnly.setSleepy(emotion.isSleepy());
                emotionOnly.setDrowsy(emotion.isDrowsy());
                emotionOnly.setApathy(emotion.isApathy());
                emotionOnly.setFrustrated(emotion.isFrustrated());
                emotionOnly.setInnocent(emotion.isInnocent());
                emotionOnly.setUnpleasant(emotion.isUnpleasant());
                emotionOnly.setSensitivity(emotion.isSensitivity());
                getPostResponse.setEmotionOnly(emotionOnly);

                return getPostResponse;
            }
        }
        return null;
    }

    public PostResponse uploadImage(MultipartFile image) throws IOException {

        String fileName = generateFileName(image);

        if (!isImageFile(fileName)) {
            throw new IllegalArgumentException("이미지 파일이 아닙니다.");
        }

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(image.getContentType());
        metadata.setContentLength(image.getSize());

        PostResponse extractedMetadata = extractImageMetadata(image);

        s3Client.putObject(bucket, fileName, image.getInputStream(), metadata);

        extractedMetadata.setImageUrl(s3Client.getUrl(bucket, fileName).toString());

        return extractedMetadata;
    }

    private boolean isImageFile(String fileName) {
        String[] allowedExtensions = {".jpg", ".jpeg", ".png", ".tiff"};

        for (String extension : allowedExtensions) {
            if (StringUtils.endsWithIgnoreCase(fileName, extension)) {
                return true;
            }
        }
        return false;
    }

    private String generateFileName(MultipartFile file) {
        return UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
    }

    private PostResponse extractImageMetadata(MultipartFile image) throws IOException {
        PostResponse postResponse = new PostResponse();

        try {
            Metadata imageMetadata = ImageMetadataReader.readMetadata(new ByteArrayInputStream(image.getBytes()));

            LocalDateTime dateTime = extractDateTime(imageMetadata);

            if (dateTime != null) {
                postResponse.setUploadTime(dateTime);
            } else {
                postResponse.setUploadTime(LocalDateTime.now());
            }

            GpsDirectory gpsDirectory = imageMetadata.getFirstDirectoryOfType(GpsDirectory.class);
            if (gpsDirectory != null) {
                GeoLocation geoLocation = gpsDirectory.getGeoLocation();
                if (geoLocation != null) {
                    postResponse.setLatitude(geoLocation.getLatitude());
                    postResponse.setLongitude(geoLocation.getLongitude());
                }
            }

            // 좌표로 주소 검색
            if ((postResponse.getLatitude() != 0.0) && (postResponse.getLongitude() != 0.0)) {
                AddressDTO addressDTO = naverReverseGeocoding(postResponse.getLatitude(), postResponse.getLongitude());
                if (addressDTO != null) {
                    postResponse.setRoadAddress(addressDTO.getRoadAddress());
                    postResponse.setAddress(addressDTO.getAddress());
                    postResponse.setBuilding(addressDTO.getBuilding());
                }
            }

        } catch (Exception e) {
            throw new IOException("이미지 메타데이터 추출 중 오류 발생: " + e.getMessage());
        }

        return postResponse;
    }

    private LocalDateTime extractDateTime(Metadata metadata) {
        // JPEG, TIFF
        ExifSubIFDDirectory exifSubIFD = metadata.getFirstDirectoryOfType(ExifSubIFDDirectory.class);
        if (exifSubIFD != null) {

            Date date = exifSubIFD.getDate(ExifSubIFDDirectory.TAG_DATETIME_ORIGINAL);
            if (date != null) {
                return date.toInstant().atZone(ZoneOffset.UTC).toLocalDateTime();
            }
        }

        // PNG
        PngDirectory pngDirectory = metadata.getFirstDirectoryOfType(PngDirectory.class);
        if (pngDirectory != null) {
            Date date = pngDirectory.getDate(PngDirectory.TAG_LAST_MODIFICATION_TIME);
            if (date != null) {
                //return date.toInstant().atZone(ZoneId.of("Asia/Seoul")).toLocalDateTime();
                return date.toInstant().atZone(ZoneOffset.UTC).toLocalDateTime();
            }
        }

        return null;
    }

    private AddressDTO naverReverseGeocoding(double latitude, double longitude) {

        AddressDTO addressDTO = new AddressDTO();

        try {
            String apiUrl = "https://naveropenapi.apigw.ntruss.com/map-reversegeocode/v2/gc?coords=" + longitude + "," + latitude + "&output=json&orders=roadaddr,addr";
            URL url = new URL(apiUrl);
            HttpURLConnection con = (HttpURLConnection)url.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("X-NCP-APIGW-API-KEY-ID", naverMapClientId);
            con.setRequestProperty("X-NCP-APIGW-API-KEY", naverMapClientSecret);

            int responseCode = con.getResponseCode();
            BufferedReader br;
            if (responseCode==200) {
                br = new BufferedReader(new InputStreamReader(con.getInputStream()));
            } else {
                br = new BufferedReader(new InputStreamReader(con.getErrorStream()));
            }

            String inputLine;
            StringBuffer response = new StringBuffer();
            while ((inputLine = br.readLine()) != null) {
                response.append(inputLine);
            }
            br.close();

            String jsonString = response.toString();

            JSONObject jsonObject = new JSONObject(jsonString);
            JSONArray results = jsonObject.getJSONArray("results");
            JSONObject firstResult = results.getJSONObject(0);

            JSONObject region = firstResult.getJSONObject("region");
            String area1 = region.getJSONObject("area1").getString("name");
            String area2 = region.getJSONObject("area2").getString("name");
            String area3 = region.getJSONObject("area3").getString("name");

            JSONObject land = firstResult.getJSONObject("land");
            String roadName = land.getString("name");
            String buildingNumber1 = land.getString("number1");
            String buildingNumber2 = land.getString("number2");
            String building = land.getJSONObject("addition0").getString("value");

            addressDTO.setRoadAddress(String.format("%s %s %s %s %s %s %s", area1, area2, area3, roadName, buildingNumber1, buildingNumber2, building));

            JSONObject secondResult = results.getJSONObject(1);

            String number1 = secondResult.getJSONObject("land").getString("number1");
            String number2 = secondResult.getJSONObject("land").getString("number2");
            addressDTO.setAddress(String.format("%s %s %s %s-%s",area1, area2, area3, number1, number2));

            addressDTO.setBuilding(building);

            return addressDTO;

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    // 순수하게 S3에만 올리기
    public List<String> uploadImageS3(List<MultipartFile> images) throws IOException {

        if (images == null) {
            return null;
        }

        List<String> imageUrls = new ArrayList<>();

        for (MultipartFile image : images) {

            String fileName = generateFileName(image);

            if (!isImageFile(fileName)) {
                throw new IllegalArgumentException("이미지 파일이 아닙니다.");
            }

            s3Client.putObject(bucket, fileName, image.getInputStream(), new ObjectMetadata());

            imageUrls.add(s3Client.getUrl(bucket, fileName).toString());
        }
        return imageUrls;
    }

    public boolean uploadPost(UploadPostDTO uploadPostDTO, List<MultipartFile> images, String loginId) throws IOException {

        Optional<User> optionalUser = userRepository.findByLoginid(loginId);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();

            double latitude = uploadPostDTO.getLatitude();
            double longitude = uploadPostDTO.getLongitude();
            LocalDateTime dateTime = uploadPostDTO.getUploadTime();
            String mainImageUrl = uploadPostDTO.getImageUrl();
            String content = uploadPostDTO.getContent();
            CategoryEnum category = uploadPostDTO.getCategory();
            EmotionEnum emotion = uploadPostDTO.getEmotion();
            PersonEnum person = uploadPostDTO.getPerson();
            IsPublicEnum isPublic = uploadPostDTO.getIsPublic();

            if (mainImageUrl == null) {
                mainImageUrl = defaultPostImage;
            }

            Post post = new Post();
            post.setImage(mainImageUrl);
            post.setLatitude(latitude);
            post.setLongitude(longitude);
            post.setUpload_time(dateTime);
            post.setContent(content);
            post.setCategory(category);
            post.setEmotion(emotion);
            post.setPerson(person);
            post.setIs_public(isPublic);
            post.setUser(user);

            Post savePost = postRepository.save(post);

            // 여러 장의 사진 최초 업로드
            if (images != null) {
                List<String> imageUrls = uploadImageS3(images);

                for (String imageUrl : imageUrls) {
                    Image image = new Image();
                    image.setPost(savePost);
                    image.setImageUrl(imageUrl);

                    try {
                        imageRepository.save(image);
                    } catch (Exception e) {
                        return false;
                    }
                }
            }
        }

        return true;
    }

    // 링크 기반 삭제 (1건)
    private void deleteImage(String image) throws IOException {

        if (image.equals(defaultPostImage)) {
            return;
        }

        try {
            URL url = new URL(image);
            String fileName = url.getPath().substring(url.getPath().lastIndexOf("/") + 1);
            s3Client.deleteObject(bucket, fileName);
        } catch (Exception e) {
            throw new IOException(e.getMessage());
        }
    }

    public String updatePost(Long id, UpdatePostRequest updatePostRequest, String loginId) throws IOException {

        Optional<Post> optionalPost = postRepository.findById(id);
        if (optionalPost.isPresent()) {
            Post prePost = optionalPost.get();

            String imageUrl = updatePostRequest.getImageUrl();
            String preImageUrl = prePost.getImage();

            List<String> deleteImages = updatePostRequest.getDeleteImages();
            List<String> addImages = updatePostRequest.getAddImages();

            List<Image> images = imageRepository.findByPost(prePost);

            if ((images.size() + addImages.size() - deleteImages.size()) > 10) {
                return "사진은 10장 까지만 등록가능합니다.";
            }

            if (!preImageUrl.equals(imageUrl)) {

                // 기존 사진 S3에서 제거
                deleteImage(preImageUrl);

                // Post 테이블에서 사진 변경
                prePost.setImage(imageUrl);

            }

            prePost.setUpload_time(updatePostRequest.getUploadTime());
            prePost.setLatitude(updatePostRequest.getLatitude());
            prePost.setLongitude(updatePostRequest.getLongitude());
            prePost.setCategory(updatePostRequest.getCategory());
            prePost.setEmotion(updatePostRequest.getEmotion());
            prePost.setPerson(updatePostRequest.getPerson());
            prePost.setContent(updatePostRequest.getContent());
            prePost.setIs_public(updatePostRequest.getIsPublic());

            for (String deleteImage : deleteImages) {
                // 조건 확인하는 것 필요함 (Image 테이블에 이 사진 주소가 있는지) 있을 경우 삭제
                Optional<Image> optionalImage = imageRepository.findByPostAndImageUrl(prePost, deleteImage);
                if (optionalImage.isPresent()) {
                    Image image = optionalImage.get();

                    imageRepository.delete(image);
                    s3Client.deleteObject(bucket, deleteImage);

                } else {
                    return "사진 삭제에 실패했습니다.";
                }
            }

            for (String addImage : addImages) {

                Image image = new Image();
                image.setPost(prePost);
                image.setImageUrl(addImage);
                imageRepository.save(image);
            }
            return "게시물 수정 성공";
        }
        return "존재하지 않는 게시물입니다.";
    }

    public String deletePost(Long id, String loginId) throws IOException {

        Optional<Post> optionalPost = postRepository.findById(id);
        if (optionalPost.isEmpty()) {
            return "존재하지 않는 게시물입니다.";
        }
        Post post = optionalPost.get();

        Optional<User> optionalUser = userRepository.findByLoginid(loginId);
        if (optionalUser.isEmpty()) {
            return "존재하지 않는 사용자입니다.";
        }

        User user = optionalUser.get();
        if (!post.getUser().getId().equals(user.getId())) {
            return "잘못된 접근입니다.";
        }

        deleteImage(post.getImage());

        List<Image> images = imageRepository.findByPost(post);
        for (Image image : images) {
            deleteImage(image.getImageUrl());
        }
        imageRepository.deleteAll(images);
        postRepository.delete(post);
        return "게시물 삭제 성공";
    }
}