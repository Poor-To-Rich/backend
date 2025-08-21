package com.poortorich.photo.repository;

import com.poortorich.chat.entity.Chatroom;
import com.poortorich.photo.entity.Photo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PhotoRepository extends JpaRepository<Photo, Long> {

    List<Photo> findTop10ByChatroomOrderByCreatedDateDesc(Chatroom chatroom);
}
