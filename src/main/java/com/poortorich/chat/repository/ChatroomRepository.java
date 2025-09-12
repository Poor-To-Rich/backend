package com.poortorich.chat.repository;

import com.poortorich.chat.entity.Chatroom;
import com.poortorich.chat.entity.enums.ChatroomRole;
import com.poortorich.user.entity.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatroomRepository extends JpaRepository<Chatroom, Long> {

    List<Chatroom> findAllByIdInAndIsClosedFalse(List<Long> chatroomIds);

    @Query("""
                SELECT c
                  FROM Chatroom c
                  JOIN ChatParticipant cp
                    ON cp.chatroom = c
                WHERE c.isClosed = false
                   AND cp.user = :user
                   AND cp.role = :role
            """)
    List<Chatroom> findChatroomByUserAndRole(@Param("user") User user, @Param("role") ChatroomRole role);

    @Query("""
                SELECT c
                  FROM Chatroom c
                  LEFT JOIN ChatMessage cm ON cm.chatroom = c
                        AND cm.messageType IN ('TEXT', 'PHOTO', 'RANKING')
                  LEFT JOIN Like l ON l.chatroom = c
                        AND l.likeStatus = true
                  LEFT JOIN ChatParticipant cp ON cp.chatroom = c
                        AND cp.isParticipated = true
                  LEFT JOIN Tag t ON t.chatroom = c
                WHERE c.isClosed = false
                GROUP BY c.id
                ORDER BY MAX(cm.sentAt) DESC,
                         COUNT(DISTINCT l.id) DESC,
                         COUNT(DISTINCT cp.id) DESC,
                         c.createdDate DESC,
                         c.id ASC
            """)
    List<Chatroom> findChatroomsSortByUpdatedAt();

    @Query("""
                SELECT c
                  FROM Chatroom c
                WHERE c.isClosed = false
                  AND c.id < :cursor
                ORDER BY c.id DESC
            """)
    Slice<Chatroom> findByCursorSortByCreatedAt(@Param("cursor") Long cursor, Pageable pageable);

    @Query("""
                SELECT c
                  FROM Chatroom c
                  LEFT JOIN ChatMessage cm ON cm.chatroom = c
                  LEFT JOIN Like l ON l.chatroom = c
                        AND l.likeStatus = true
                  LEFT JOIN ChatParticipant cp ON cp.chatroom = c
                        AND cp.isParticipated = true
                  LEFT JOIN Tag t ON t.chatroom = c
                WHERE c.isClosed = false
                GROUP BY c.id
                ORDER BY COUNT(DISTINCT l.id) DESC,
                         COUNT(DISTINCT cp.id) DESC,
                         c.createdDate DESC,
                         c.id ASC
            """)
    List<Chatroom> findChatroomsSortByLike();

    @Query("""
                SELECT DISTINCT c
                  FROM Chatroom c
                  LEFT JOIN Tag t ON t.chatroom = c
                 INNER JOIN ChatParticipant cp ON cp.chatroom = c AND cp.role = 'HOST'
                WHERE c.isClosed = false
                  AND :keyword <> ''
                  AND (
                         c.title LIKE CONCAT('%', :keyword, '%')
                      OR c.description LIKE CONCAT('%', :keyword, '%')
                      OR cp.user.nickname LIKE CONCAT('%', :keyword, '%')
                      OR (t.name IS NOT NULL AND t.name LIKE CONCAT('%', :keyword, '%'))
                  )
                GROUP BY c.id
            """)
    List<Chatroom> searchChatrooms(String keyword);

    @Query(value = """
            SELECT c.id
            FROM chatroom c
            JOIN chat_participant cp ON cp.chatroom_id = c.id
            WHERE cp.user_id = :userId
            AND cp.is_participated = true
            ORDER BY c.id ASC
            LIMIT 1
            """, nativeQuery = true)
    Long findFirstChatroomIdByUser(@Param("userId") Long userId);

    List<Chatroom> findAllByIsRankingEnabledIsTrueAndIsClosedIsFalse();
}
