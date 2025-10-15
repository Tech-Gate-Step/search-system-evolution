package tech.gate.step.searchsystemevolution.es.domain;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.*;
import tech.gate.step.searchsystemevolution.domain.product.ProductEntity;

@Getter
@Document(indexName = "products")  // 인덱스 이름
@Setting(settingPath = "/elasticsearch/product-settings.json")  // 한글 분석기 설정
public class Product {

    @Id
    private Long id;

    @Field(type = FieldType.Keyword)
    private String sku;  // sku 코드

    @MultiField(
            mainField = @Field(type = FieldType.Text, analyzer = "nori"),
            otherFields = {
                    @InnerField(suffix = "keyword", type = FieldType.Keyword),
                    @InnerField(suffix = "ngram", type = FieldType.Text, analyzer = "ngram_analyzer")
            }
    )
    private String name; // 상품 명

    @Field(type = FieldType.Text, analyzer = "nori")
    private String description;

    // 브랜드 명
    @Field(type = FieldType.Keyword)
    private String brand;

    // 카테고리 ID
    @Field(type = FieldType.Integer)
    private Integer categoryId;

    // 가격
    @Field(type = FieldType.Integer)
    private Integer price;

    // 재고 수량
    @Field(type = FieldType.Integer)
    private Integer stock;

    // 평점 (1.0 ~ 5.0)
    @Field(type = FieldType.Double)
    private Double rating;

    // 판매량
    @Field(type = FieldType.Integer)
    private Integer salesCount;

    @Builder
    public Product(Long id, String sku, String name, String description, String brand, Integer categoryId, Integer price, Integer stock, Double rating, Integer salesCount) {
        this.id = id;
        this.sku = sku;
        this.name = name;
        this.description = description;
        this.brand = brand;
        this.categoryId = categoryId;
        this.price = price;
        this.stock = stock;
        this.rating = rating;
        this.salesCount = salesCount;
    }

    public static Product from(ProductEntity productEntity) {
        return Product.builder()
                .id(productEntity.getId())
                .sku(productEntity.getSku())
                .name(productEntity.getName())
                .description(productEntity.getDescription())
                .brand(productEntity.getBrand())
                .categoryId(productEntity.getCategoryId())
                .price(productEntity.getPrice())
                .stock(productEntity.getStock())
                .rating(productEntity.getRating())
                .salesCount(productEntity.getSalesCount())
                .build();
    }

}
