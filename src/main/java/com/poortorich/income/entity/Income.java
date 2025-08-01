package com.poortorich.income.entity;

import com.poortorich.accountbook.entity.AccountBook;
import com.poortorich.accountbook.entity.enums.IterationType;
import com.poortorich.accountbook.enums.AccountBookType;
import com.poortorich.category.entity.Category;
import com.poortorich.expense.entity.enums.PaymentMethod;
import com.poortorich.iteration.entity.Iteration;
import com.poortorich.iteration.entity.IterationIncomes;
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
import java.util.Objects;
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
@Table(name = "income")
public class Income implements AccountBook {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "income_date", nullable = false)
    private LocalDate incomeDate;

    @Column(name = "title")
    private String title;

    @Column(name = "cost")
    private Long cost;

    @Column(name = "memo")
    private String memo;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "iteration_type", nullable = false)
    private IterationType iterationType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @OneToOne(mappedBy = "generatedIncome")
    private IterationIncomes generatedIterationIncomes;

    @CreationTimestamp
    @Column(name = "created_date")
    private LocalDateTime createdDate;

    @UpdateTimestamp
    @Column(name = "updated_date")
    private LocalDateTime updatedDate;

    @Override
    public LocalDate getAccountBookDate() {
        return incomeDate;
    }

    @Override
    public Iteration getGeneratedIteration() {
        return generatedIterationIncomes;
    }

    @Override
    public PaymentMethod getPaymentMethod() {
        return null;
    }

    @Override
    public AccountBookType getType() {
        return AccountBookType.INCOME;
    }
    
    @Override
    public void updateAccountBook(String title, Long cost, String memo, IterationType iterationType,
                                  Category category) {
        this.title = title;
        this.cost = cost;
        this.memo = memo;
        this.iterationType = iterationType;
        this.category = category;
    }

    @Override
    public void updateAccountBookDate(LocalDate accountBookDate) {
        this.incomeDate = accountBookDate;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (Objects.isNull(obj) || getClass() != obj.getClass()) {
            return false;
        }
        Income that = (Income) obj;

        if (!Objects.isNull(id) && !Objects.isNull(that.id)) {
            return Objects.equals(id, that.id);
        }

        return Objects.equals(title, that.title)
                && Objects.equals(cost, that.cost)
                && Objects.equals(incomeDate, that.incomeDate)
                && Objects.equals(user, that.user)
                && Objects.equals(category, that.category);
    }

    @Override
    public int hashCode() {
        if (!Objects.isNull(id)) {
            return Objects.hash(id);
        }
        return Objects.hash(title, cost, incomeDate, user, category);
    }
}
