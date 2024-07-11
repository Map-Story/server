package com.team13.mapstory.service;

import com.team13.mapstory.dto.emotion.EmotionRequest;
import com.team13.mapstory.dto.emotion.EmotionResponse;
import com.team13.mapstory.entity.Emotion;
import com.team13.mapstory.entity.User;
import com.team13.mapstory.repository.EmotionRepository;
import com.team13.mapstory.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EmotionService {

    private final EmotionRepository emotionRepository;
    private final UserRepository userRepository;

    public boolean addEmotion(String loginId, EmotionRequest emotionRequest) {

        boolean happy = emotionRequest.isHappy();
        boolean sad = emotionRequest.isSad();
        boolean depressed = emotionRequest.isDepressed();
        boolean stress = emotionRequest.isStress();
        boolean angry = emotionRequest.isAngry();
        boolean sleepy = emotionRequest.isSleepy();
        boolean drowsy = emotionRequest.isDrowsy();
        boolean apathy = emotionRequest.isApathy();
        boolean frustrated = emotionRequest.isFrustrated();
        boolean innocent = emotionRequest.isInnocent();
        boolean unpleasant = emotionRequest.isUnpleasant();
        boolean sensitive = emotionRequest.isSensitive();

        Optional<User> optionalUser = userRepository.findByLoginid(loginId);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();

            Emotion emotion = new Emotion();
            emotion.setUser(user);
            emotion.setHappy(happy);
            emotion.setSad(sad);
            emotion.setDepressed(depressed);
            emotion.setStress(stress);
            emotion.setAngry(angry);
            emotion.setSleepy(sleepy);
            emotion.setDrowsy(drowsy);
            emotion.setApathy(apathy);
            emotion.setFrustrated(frustrated);
            emotion.setInnocent(innocent);
            emotion.setUnpleasant(unpleasant);
            emotion.setSensitive(sensitive);

            emotionRepository.save(emotion);
            return true;
        }
        return false;
    }

    public boolean updateEmotion(String loginId, EmotionRequest emotionRequest) {

        boolean happy = emotionRequest.isHappy();
        boolean sad = emotionRequest.isSad();
        boolean depressed = emotionRequest.isDepressed();
        boolean stress = emotionRequest.isStress();
        boolean angry = emotionRequest.isAngry();
        boolean sleepy = emotionRequest.isSleepy();
        boolean drowsy = emotionRequest.isDrowsy();
        boolean apathy = emotionRequest.isApathy();
        boolean frustrated = emotionRequest.isFrustrated();
        boolean innocent = emotionRequest.isInnocent();
        boolean unpleasant = emotionRequest.isUnpleasant();
        boolean sensitive = emotionRequest.isSensitive();

        Optional<User> optionalUser = userRepository.findByLoginid(loginId);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();

            Optional<Emotion> optionalEmotion = emotionRepository.findByUser(user);
            if (optionalEmotion.isPresent()) {
                Emotion emotion = optionalEmotion.get();

                emotion.setHappy(happy);
                emotion.setSad(sad);
                emotion.setDepressed(depressed);
                emotion.setStress(stress);
                emotion.setAngry(angry);
                emotion.setSleepy(sleepy);
                emotion.setDrowsy(drowsy);
                emotion.setApathy(apathy);
                emotion.setFrustrated(frustrated);
                emotion.setInnocent(innocent);
                emotion.setUnpleasant(unpleasant);
                emotion.setSensitive(sensitive);

                emotionRepository.save(emotion);
                return true;

            } else {

                Emotion emotion = new Emotion();
                emotion.setUser(user);
                emotion.setHappy(happy);
                emotion.setSad(sad);
                emotion.setDepressed(depressed);
                emotion.setStress(stress);
                emotion.setAngry(angry);
                emotion.setSleepy(sleepy);
                emotion.setDrowsy(drowsy);
                emotion.setApathy(apathy);
                emotion.setFrustrated(frustrated);
                emotion.setInnocent(innocent);
                emotion.setUnpleasant(unpleasant);
                emotion.setSensitive(sensitive);

                emotionRepository.save(emotion);
                return true;
            }
        }
        return false;
    }


    public EmotionResponse getEmotion(String loginId) {

        Optional<User> optionalUser = userRepository.findByLoginid(loginId);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();

            Optional<Emotion> optionalEmotion = emotionRepository.findByUser(user);
            if (optionalEmotion.isPresent()) {
                Emotion emotion = optionalEmotion.get();

                EmotionResponse emotionResponse = new EmotionResponse();
                emotionResponse.setHappy(emotion.isHappy());
                emotionResponse.setSad(emotion.isSad());
                emotionResponse.setDepressed(emotion.isDepressed());
                emotionResponse.setStress(emotion.isStress());
                emotionResponse.setAngry(emotion.isAngry());
                emotionResponse.setSleepy(emotion.isSleepy());
                emotionResponse.setDrowsy(emotion.isDrowsy());
                emotionResponse.setApathy(emotion.isApathy());
                emotionResponse.setFrustrated(emotion.isFrustrated());
                emotionResponse.setInnocent(emotion.isInnocent());
                emotionResponse.setUnpleasant(emotion.isUnpleasant());
                emotionResponse.setSensitive(emotion.isSensitive());
                return emotionResponse;
            }
        }
        return null;
    }
}
