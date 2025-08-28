package com.poortorich.ranking.model;

import com.poortorich.chat.entity.ChatParticipant;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class Rankers {

    private static int FIRST = 0;
    private static int SECOND = 1;
    private static int THIRD = 2;

    List<ChatParticipant> savers;
    List<ChatParticipant> flexers;

    private boolean isInvalidAccess(List<ChatParticipant> participants, int index) {
        return participants == null || participants.size() <= index;
    }

    public Long firstSaver() {
        if (isInvalidAccess(savers, FIRST)) {
            return null;
        }
        return savers.get(FIRST).getUser().getId();
    }

    public Long secondSaver() {
        if (isInvalidAccess(savers, SECOND)) {
            return null;
        }
        return savers.get(SECOND).getUser().getId();
    }

    public Long thirdSaver() {
        if (isInvalidAccess(savers, THIRD)) {
            return null;
        }
        return savers.get(THIRD).getUser().getId();
    }

    public Long firstFlexer() {
        if (isInvalidAccess(flexers, FIRST)) {
            return null;
        }
        return flexers.get(FIRST).getUser().getId();
    }

    public Long secondFlexer() {
        if (isInvalidAccess(flexers, SECOND)) {
            return null;
        }
        return flexers.get(SECOND).getUser().getId();
    }

    public Long thirdFlexer() {
        if (isInvalidAccess(flexers, THIRD)) {
            return null;
        }
        return flexers.get(THIRD).getUser().getId();
    }
}
