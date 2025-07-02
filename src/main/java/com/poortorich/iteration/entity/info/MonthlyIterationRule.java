package com.poortorich.iteration.entity.info;

import com.poortorich.iteration.entity.enums.Weekday;
import com.poortorich.iteration.entity.enums.MonthlyMode;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.DynamicUpdate;

@Entity
@Getter
@SuperBuilder
@DynamicUpdate
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@DiscriminatorValue("MONTHLY")
public class MonthlyIterationRule extends IterationInfo {

    @Enumerated(EnumType.STRING)
    @Column(name = "monthlyMode")
    private MonthlyMode monthlyMode;

    @Column(name = "monthlyDay")
    private Integer monthlyDay;

    @Column(name = "monthlyWeek")
    private Integer monthlyWeek;

    @Enumerated(EnumType.STRING)
    @Column(name = "monthlyDayOfWeek")
    private Weekday monthlyDayOfWeek;
}
