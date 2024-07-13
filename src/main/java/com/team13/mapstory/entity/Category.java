package com.team13.mapstory.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    private User user;

    // 맛집, 카페, 데이트 코스, 산책로
    // true 이면 사용자가 사용하기로 결정, false 이면 사용자가 사용하지 않기로 결정
    private boolean restaurant;
    private boolean cafe;
    private boolean date;
    private boolean trail;

    public Category() {
        this.restaurant = false;
        this.cafe = false;
        this.date = false;
        this.trail = false;
    }
}
