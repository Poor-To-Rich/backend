package com.poortorich.iteration.entity.info;

import com.poortorich.iteration.entity.enums.EndType;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.DiscriminatorType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@SuperBuilder
@DynamicUpdate
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "iterationType", discriminatorType = DiscriminatorType.STRING)
@Table(name = "iterationInfo")
public class IterationInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "iterationType", insertable = false, updatable = false)
    private String iterationType;

    @Column(name = "cycle")
    private Integer cycle;

    @Enumerated(EnumType.STRING)
    @Column(name = "endType")
    private EndType endType;

    @Column(name = "endCount")
    private Integer endCount;

    @Column(name = "endDate")
    private LocalDate endDate;

    @CreationTimestamp
    @Column(name = "createdDate")
    private LocalDateTime createdDate;

    @UpdateTimestamp
    @Column(name = "updatedDate")
    private LocalDateTime updatedDate;

    public String getIterationTypeLowerCase() {
        return iterationType.toLowerCase();
    }
}
