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
import com.team13.mapstory.dto.post.RequestPost;
import com.team13.mapstory.dto.post.UploadPostDTO;
import com.team13.mapstory.entity.Image;
import com.team13.mapstory.entity.Post;
import com.team13.mapstory.entity.User;
import com.team13.mapstory.repository.ImageRepository;
import com.team13.mapstory.repository.PostRepository;
import com.team13.mapstory.repository.UserRepository;
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
    @Value("${naver-map-api.client-id}")
    private String naverMapClientId;

    @Value("${naver-map-api.client-secret}")
    private String naverMapClientSecret;

    private final AmazonS3Client s3Client;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    public List<Post> getAllPosts(String loginId) {

        Optional<User> optionalUser = userRepository.findByLoginid(loginId);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();

            List<Post> posts = postRepository.findAllByUser(user);
            if (posts.isEmpty()) {
                return null;
            } return posts;
        }
        return null;
    }

    public Post getPostById(Long id, String loginId) {

        User user = null;

        Optional<User> optionalUser = userRepository.findByLoginid(loginId);
        if (optionalUser.isPresent()) {
            user = optionalUser.get();
        }

        Optional<Post> optionalPost = postRepository.findById(id);
        if (optionalPost.isPresent()) {
            Post post = optionalPost.get();

            if (post.getUser() == user) {
                return post;
            }
        }

        return null;
    }

    public RequestPost uploadImage(MultipartFile image) throws IOException {

        String fileName = generateFileName(image);

        if (!isImageFile(fileName)) {
            throw new IllegalArgumentException("이미지 파일이 아닙니다.");
        }

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(image.getContentType());
        metadata.setContentLength(image.getSize());

        RequestPost extractedMetadata = extractImageMetadata(image);

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

    private RequestPost extractImageMetadata(MultipartFile image) throws IOException {
        RequestPost requestPost = new RequestPost();

        try {
            Metadata imageMetadata = ImageMetadataReader.readMetadata(new ByteArrayInputStream(image.getBytes()));

            LocalDateTime dateTime = extractDateTime(imageMetadata);

            if (dateTime != null) {
                requestPost.setUploadTime(dateTime);
            } else {
                requestPost.setUploadTime(LocalDateTime.now());
            }

            GpsDirectory gpsDirectory = imageMetadata.getFirstDirectoryOfType(GpsDirectory.class);
            if (gpsDirectory != null) {
                GeoLocation geoLocation = gpsDirectory.getGeoLocation();
                if (geoLocation != null) {
                    requestPost.setLatitude(geoLocation.getLatitude());
                    requestPost.setLongitude(geoLocation.getLongitude());
                }
            }

            // 좌표로 주소 검색
            if ((requestPost.getLatitude() != 0.0) && (requestPost.getLongitude() != 0.0)) {
                AddressDTO addressDTO = naverReverseGeocoding(requestPost.getLatitude(), requestPost.getLongitude());
                requestPost.setAddressDTO(addressDTO);
            }


        } catch (Exception e) {
            throw new IOException("이미지 메타데이터 추출 중 오류 발생: " + e.getMessage());
        }

        return requestPost;
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

    // 순수하게 S3에만 올리기
    private List<String> uploadImageS3(List<MultipartFile> images) throws IOException {

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

    public boolean uploadPost(UploadPostDTO uploadPostDTO, String loginId) throws IOException {

        Optional<User> optionalUser = userRepository.findByLoginid(loginId);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();

            double latitude = uploadPostDTO.getLatitude();
            double longitude = uploadPostDTO.getLongitude();
            List<MultipartFile> images = uploadPostDTO.getMultipartFiles();
            LocalDateTime dateTime = uploadPostDTO.getUploadTime();
            String mainImageUrl = uploadPostDTO.getImageUrl();
            String content = uploadPostDTO.getContent();
            String category = uploadPostDTO.getCategory();
            String emotion = uploadPostDTO.getEmotion();
            String person = uploadPostDTO.getPerson();
            String weather = uploadPostDTO.getWeather();
            String isPublic = uploadPostDTO.getIsPublic();

            List<String> imageUrls = uploadImageS3(images);

            Post post = new Post();
            post.setImage(mainImageUrl);
            post.setLatitude(latitude);
            post.setLongitude(longitude);
            post.setUpload_time(dateTime);
            post.setContent(content);
            post.setCategory(category);
            post.setEmotion(emotion);
            post.setPerson(person);
            post.setWeather(weather);
            post.setIs_public(isPublic);
            post.setUser(user);

            Post savePost = postRepository.save(post);

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

        return true;
    }

    // 링크 기반 삭제
    private void deleteImage(String image) throws IOException {
        try {
            URL url = new URL(image);
            String fileName = url.getPath().substring(url.getPath().lastIndexOf("/") + 1);
            s3Client.deleteObject(bucket, fileName);
        } catch (Exception e) {
            throw new IOException(e.getMessage());
        }
    }

    public boolean updatePost(Long id, UploadPostDTO uploadPostDTO, String loginId) throws IOException {

        User user = null;

        Optional<User> optionalUser = userRepository.findByLoginid(loginId);
        if (optionalUser.isPresent()) {
            user = optionalUser.get();
        }

        // 수정되기 전 글 정보 불러오기
        Optional<Post> optionalPost = postRepository.findById(id);
        if (optionalPost.isEmpty()) return false;
        Post post = optionalPost.get();

        if (post.getUser() != user) return false;

        // 만약 메인 사진이 교체되었다면? 과거 이미지 S3에서 삭제
        String previousImageUrl = post.getImage();
        String imageUrl = uploadPostDTO.getImageUrl();

        if (!previousImageUrl.equals(imageUrl)) {
            deleteImage(previousImageUrl);
            post.setImage(imageUrl);
        }

        // 메인 사진 말고 다른 사진이 교체되었다면?
        List<MultipartFile> images = uploadPostDTO.getMultipartFiles(); // 새로운 사진
// TODO:


        post.setLatitude(uploadPostDTO.getLatitude());
        post.setLongitude(uploadPostDTO.getLongitude());
        post.setUpload_time(uploadPostDTO.getUploadTime());
        post.setContent(uploadPostDTO.getContent());
        post.setCategory(uploadPostDTO.getCategory());
        post.setEmotion(uploadPostDTO.getEmotion());
        post.setPerson(uploadPostDTO.getPerson());
        post.setWeather(uploadPostDTO.getWeather());
        post.setIs_public(uploadPostDTO.getIsPublic());
        post.setUser(user);

        try {
            postRepository.save(post);
            return true;
        } catch (Exception e) {
            return false;
        }


    }

    public boolean deletePost(Long id, String loginId) throws IOException {

        User user = null;

        Optional<User> optionalUser = userRepository.findByLoginid(loginId);
        if (optionalUser.isPresent()) {
            user = optionalUser.get();
        }

        Optional<Post> optionalPost = postRepository.findById(id);
        if (optionalPost.isEmpty()) return false;
        Post post = optionalPost.get();

        if (post.getUser() != user) return false;

        String image = post.getImage();

        if (image != null) {
            deleteImage(image);
        }

        try {
            postRepository.delete(post);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}