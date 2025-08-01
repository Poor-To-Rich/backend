package com.poortorich.tag.repository;

import com.poortorich.chat.entity.Chatroom;
import com.poortorich.tag.entity.Tag;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TagRepository extends JpaRepository<Tag, Long> {

    List<Tag> findByChatroom(Chatroom chatroom);

    void deleteAllByChatroom(Chatroom chatroom);
}
