package PoorToRich.PoorToRich.category.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.Id;

import java.math.BigInteger;
import java.sql.Timestamp;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "category")
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private CategoryType type;

    private String name;

    private String color;

    private Boolean visibility;

    private Timestamp createdDate;

    private Timestamp updatedDate;

    // User 외래키
}
