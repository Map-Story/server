package com.team13.mapstory.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@ToString
public class Friend {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fromFriend", referencedColumnName = "id", nullable = false )
    private User requestUser;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "toFriend", referencedColumnName = "id", nullable = false)
    private User responseUser;

    @Enumerated(EnumType.STRING)
    @Column(name = "is_friend", nullable = false)
    private FriendStatus status;

    public enum FriendStatus{
        PENDING, // 대기 중
        ACCEPTED, // 수락됨
        REJECTED // 거절됨
    }
}
