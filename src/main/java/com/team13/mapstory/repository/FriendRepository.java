package com.team13.mapstory.repository;

import com.team13.mapstory.entity.Friend;
import com.team13.mapstory.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FriendRepository extends JpaRepository<Friend,Long> {
    Optional<List<Friend>> findAllByResponseUser(User user);
    Optional<List<Friend>> findAllByRequestUserOrResponseUserAndStatus(User user1, User user2, Friend.FriendStatus status);

    @Query("SELECT f FROM Friend f WHERE (f.requestUser = :user AND f.responseUser = :friend) OR (f.requestUser = :friend AND f.responseUser = :user)")
    Optional<Friend> checkFriendRelationship(@Param("user") User user, @Param("friend") User friend);
}

