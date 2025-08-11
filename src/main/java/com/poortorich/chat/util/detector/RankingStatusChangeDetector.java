package com.poortorich.chat.util.detector;

import java.util.Objects;
import org.springframework.stereotype.Component;

@Component
public class RankingStatusChangeDetector {

    public Boolean detectRankingChange(Boolean currentStatus, Boolean newStatus) {
        return !Objects.equals(currentStatus, newStatus);
    }
}
