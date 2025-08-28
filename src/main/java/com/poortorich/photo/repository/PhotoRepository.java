package com.poortorich.photo.repository;

import com.poortorich.chat.entity.Chatroom;
import com.poortorich.photo.entity.Photo;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PhotoRepository extends JpaRepository<Photo, Long> {

    List<Photo> findTop10ByChatroomOrderByCreatedDateDescIdAsc(Chatroom chatroom);

    @Query("""
                SELECT p
                  FROM Photo p
                 WHERE p.chatroom = :chatroom
                   AND (
                          p.createdDate < :date
                       OR (p.createdDate = :date AND p.id < :id)
                   )
                 ORDER BY p.createdDate DESC, p.id DESC
            """)
    Slice<Photo> findAllByChatroomAndCursor(
            @Param("chatroom") Chatroom chatroom,
            @Param("date") LocalDateTime date,
            @Param("id") Long id,
            Pageable pageable
    );

    Optional<Photo> findByPhotoUrl(String content);
}
