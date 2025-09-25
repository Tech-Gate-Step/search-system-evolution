package tech.gate.step.searchsystemevolution.domain.product;


import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@Table(name = "products")
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProductEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 20)
    private String sku;  // sku 코드

    @Column(nullable = false, length = 255)
    private String name; // 상품 명

    @Column(columnDefinition = "TEXT")
    private String description;

    // 브랜드 명
    private String brand;
    // 카테고리 ID
    private Integer categoryId;
    // 가격
    private Integer price;
    // 재고 수량
    private Integer stock;
    // 평점 (1.0 ~ 5.0)
    private Double rating;
    // 판매량
    private Integer salesCount;
}
