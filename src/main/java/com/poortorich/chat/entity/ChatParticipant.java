package com.poortorich.chat.entity;

import com.poortorich.chat.entity.enums.ChatroomRole;
import com.poortorich.chat.entity.enums.NoticeStatus;
import com.poortorich.chat.entity.enums.RankingStatus;
import com.poortorich.user.entity.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
@Table(name = "chat_participant")
public class ChatParticipant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private ChatroomRole role;

    @Column(name = "is_participated", nullable = false)
    private Boolean isParticipated;

    @Column(name = "leave_time")
    private LocalDateTime leaveTime;

    @Enumerated(EnumType.STRING)
    @Column(name = "ranking_status", nullable = false)
    private RankingStatus rankingStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "notice_status", nullable = false)
    private NoticeStatus noticeStatus;

    @CreationTimestamp
    @Column(name = "created_date")
    private LocalDateTime createdDate;

    @CreationTimestamp
    @Column(name = "joinAt")
    private LocalDateTime joinAt;

    @UpdateTimestamp
    @Column(name = "updated_date")
    private LocalDateTime updatedDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chatroom_id")
    private Chatroom chatroom;

    public void restoreParticipation() {
        this.isParticipated = Boolean.TRUE;
        this.leaveTime = null;
        this.joinAt = LocalDateTime.now();
    }

    public void leave() {
        this.isParticipated = Boolean.FALSE;
        this.leaveTime = LocalDateTime.now();
    }

    public void updateNoticeStatus(NoticeStatus status) {
        this.noticeStatus = status;
    }

    public void updateChatroomRole(ChatroomRole chatroomRole) {
        this.role = chatroomRole;
    }

    public void kick() {
        this.role = ChatroomRole.BANNED;
    }

    public void updateRankingStatus(RankingStatus rankingStatus) {
        this.rankingStatus = rankingStatus;
    }
}
