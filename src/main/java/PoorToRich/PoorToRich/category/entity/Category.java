package PoorToRich.PoorToRich.category.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.Id;

import java.math.BigInteger;
import java.sql.Timestamp;

@Entity
@Getter @Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "category")
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private BigInteger id;

    @Enumerated(EnumType.STRING)
    private CategoryType type;

    private String name;

    private String color;

    private boolean visibility;

    private Timestamp createdDate;

    private Timestamp updatedDate;
}
