package com.poortorich.expense.entity;

import com.poortorich.accountbook.entity.AccountBook;
import com.poortorich.accountbook.entity.enums.IterationType;
import com.poortorich.category.entity.Category;
import com.poortorich.expense.entity.enums.PaymentMethod;
import com.poortorich.iteration.entity.IterationExpenses;
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
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Getter
@Builder
@DynamicUpdate
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "expense")
public class Expense implements AccountBook {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "expenseDate", nullable = false)
    private LocalDate expenseDate;

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categoryId")
    private Category category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId")
    private User user;

    @OneToOne(mappedBy = "generatedExpense")
    private IterationExpenses generatedIterationExpenses;

    @CreationTimestamp
    @Column(name = "createdDate")
    private LocalDateTime createdDate;

    @UpdateTimestamp
    @Column(name = "updatedDate")
    private LocalDateTime updatedDate;

    @Override
    public LocalDate getAccountBookDate() {
        return expenseDate;
    }

    public void updateExpense(String title, Long cost, PaymentMethod paymentMethod, String memo,
                              IterationType iterationType, Category category) {
        this.title = title;
        this.cost = cost;
        this.paymentMethod = paymentMethod;
        this.memo = memo;
        this.iterationType = iterationType;
        this.category = category;
    }

    public void updateExpenseDate(LocalDate expenseDate) {
        this.expenseDate = expenseDate;
    }
}
