package com.poortorich.report.repository;

import com.poortorich.chat.entity.ChatParticipant;
import com.poortorich.chat.entity.Chatroom;
import com.poortorich.report.entity.Report;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReportRepository extends JpaRepository<Report, Long> {

    boolean existsByReporterAndReportedAndChatroom(
            ChatParticipant reporterMember,
            ChatParticipant reportedMember,
            Chatroom chatroom
    );
}
