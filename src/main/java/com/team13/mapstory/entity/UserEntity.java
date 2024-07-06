package com.team13.mapstory.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Entity
@Getter
@Setter
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nickname;
    private String profileimage;
    private String logintype;
    private String loginid;
    private String uid;
    private String refreshToken;

    public UserEntity() {
        this.uid = UUID.randomUUID().toString();
    }

}
