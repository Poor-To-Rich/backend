package com.poortorich.expense.entity;

import com.poortorich.category.entity.Category;
import com.poortorich.expense.entity.enums.IterationType;
import com.poortorich.expense.entity.enums.PaymentMethod;
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
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.UpdateTimestamp;

import java.sql.Timestamp;
import java.util.Date;

@Entity
@Getter
@Builder
@DynamicUpdate
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "expense")
public class Expense {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "expenseDate", nullable = false)
    private Date expenseDate;

    @Column(name = "title")
    private String title;

    @Column(name = "cost", nullable = false)
    private Long cost;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "paymentMethod", nullable = false)
    private PaymentMethod paymentMethod;

    @Column(name = "memo")
    private String memo;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "iterationType", nullable = false)
    private IterationType iterationType;

    @CreationTimestamp
    @Column(name = "createdDate")
    private Timestamp createdDate;

    @UpdateTimestamp
    @Column(name = "updateDate")
    private Timestamp updatedDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categoryId")
    private Category category;

    // User 외래키
}
