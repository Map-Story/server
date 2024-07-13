package com.team13.mapstory.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Emotion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    private User user;

    // 행복, 슬픔, 우울함, 스트레스, 화남, 졸림, 나른, 무감정, 답답, 억울, 불쾌함, 예민
    // true 이면 사용자가 사용하기로 결정, false 이면 사용자가 사용하지 않기로 결정
    private boolean happy;
    private boolean sad;
    private boolean depressed;
    private boolean stress;
    private boolean angry;
    private boolean sleepy;
    private boolean drowsy;
    private boolean apathy;
    private boolean frustrated;
    private boolean innocent;
    private boolean unpleasant;
    private boolean sensitivity;

    public Emotion() {
        this.happy = false;
        this.sad = false;
        this.depressed = false;
        this.stress = false;
        this.angry = false;
        this.sleepy = false;
        this.drowsy = false;
        this.apathy = false;
        this.frustrated = false;
        this.innocent = false;
        this.unpleasant = false;
        this.sensitivity = false;
    }
}
