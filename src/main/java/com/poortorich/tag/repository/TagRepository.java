package com.poortorich.tag.repository;

import com.poortorich.chat.entity.Chatroom;
import com.poortorich.tag.entity.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TagRepository extends JpaRepository<Tag, Long> {

    List<Tag> findByChatroom(Chatroom chatroom);

    void deleteByChatroom(Chatroom chatroom);
}
