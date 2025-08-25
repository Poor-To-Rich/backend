package com.poortorich.chat.entity;

import com.poortorich.chat.request.ChatroomUpdateRequest;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@DynamicUpdate
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "chatroom")
public class Chatroom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "image", nullable = false)
    private String image;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "max_member_count", nullable = false)
    private Long maxMemberCount;

    @Column(name = "password")
    private String password;

    @Column(name = "is_ranking_enabled", nullable = false)
    private Boolean isRankingEnabled;

    @Column(name = "is_deleted", nullable = false)
    @Builder.Default
    private Boolean isClosed = Boolean.FALSE;

    @Column(name = "delete_at")
    private LocalDateTime closeAt;

    @CreationTimestamp
    @Column(name = "created_date")
    private LocalDateTime createdDate;

    @UpdateTimestamp
    @Column(name = "updated_date")
    private LocalDateTime updatedDate;

    public void updateChatroom(ChatroomUpdateRequest chatroomUpdateRequest, String imageUrl) {
        this.image = imageUrl;
        this.title = chatroomUpdateRequest.getChatroomTitle();
        this.description = chatroomUpdateRequest.getDescription();
        this.password = chatroomUpdateRequest.getChatroomPassword();
        this.maxMemberCount = chatroomUpdateRequest.getMaxMemberCount();
        this.isRankingEnabled = chatroomUpdateRequest.getIsRankingEnabled();
    }

    public void closeChatroom() {
        isClosed = Boolean.TRUE;
        closeAt = LocalDateTime.now();
    }
}
