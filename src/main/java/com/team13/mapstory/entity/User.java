package com.team13.mapstory.entity;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@jakarta.persistence.Entity
@Getter
@Setter
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nickname;
    private String profileimage;
    private String logintype;
    private String loginid;
    private String uid;
    private String refreshToken;

    public User() {
        this.uid = UUID.randomUUID().toString();
    }

}
