package com.team13.mapstory.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username; // 예시에 있어서 일단 넣어봄
    private String name;
    private String email;
    private String role;

    private String profileimage;
    private String logintype;
    private String loginid;
    private String uid;
    private String refreshToken; // 없어야 할것 같기도?

}
