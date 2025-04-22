package com.poortorich.iteration.entity.info;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.DynamicUpdate;

@Entity
@Getter
@SuperBuilder
@DynamicUpdate
@NoArgsConstructor
@AllArgsConstructor
@DiscriminatorValue("WEEKLY")
public class WeeklyIterationRule extends IterationInfo {

    @Column(name = "daysOfWeek")
    private String daysOfWeek;
}
