package com.team13.mapstory.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String email;

    private String profile_image;

    @Column(nullable = false)
    private String login_type;

    @Column(nullable = false)
    private String login_id;

    @Column(nullable = false)
    private String user_code;

    @Column(nullable = false)
    private String refresh_token;
}
