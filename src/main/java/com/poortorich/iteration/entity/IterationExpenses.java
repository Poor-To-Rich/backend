package com.poortorich.iteration.entity;

import com.poortorich.accountbook.entity.AccountBook;
import com.poortorich.expense.entity.Expense;
import com.poortorich.iteration.entity.info.IterationInfo;
import com.poortorich.user.entity.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
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
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(name = "iteration_expenses")
public class IterationExpenses implements Iteration {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "original_expense_id")
    private Expense originalExpense;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "generated_expense_id")
    private Expense generatedExpense;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "iteration_info_id")
    private IterationInfo iterationInfo;

    @CreationTimestamp
    @Column(name = "created_date")
    private LocalDateTime createdDate;

    @UpdateTimestamp
    @Column(name = "updated_date")
    private LocalDateTime updatedDate;

    @Override
    public AccountBook getOriginalAccountBook() {
        return originalExpense;
    }

    @Override
    public AccountBook getGeneratedAccountBook() {
        return generatedExpense;
    }

    @Override
    public void updateOriginalAccountBook(AccountBook originalExpense) {
        this.originalExpense = (Expense) originalExpense;
    }
}
