package com.poortorich.like.repository;

import com.poortorich.chat.entity.Chatroom;
import com.poortorich.like.entity.Like;
import com.poortorich.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LikeRepository extends JpaRepository<Like, Long> {

    Optional<Like> findByUserAndChatroom(User user, Chatroom chatroom);

    Long countByChatroomAndLikeStatusTrue(Chatroom chatroom);

    void deleteByChatroom(Chatroom chatroom);
}
