package tech.gate.step.searchsystemevolution.es.controller;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tech.gate.step.searchsystemevolution.domain.product.ProductEntity;
import tech.gate.step.searchsystemevolution.es.service.ProductSyncService;
import tech.gate.step.searchsystemevolution.service.dto.ProductSummary;

@RestController
@RequestMapping("/api/v1/p")
@RequiredArgsConstructor
public class ProductController {

    private final ProductSyncService productSyncService;


    @PostMapping
    public ResponseEntity<ProductSummary> createProduct() {
        return ResponseEntity.ok(null);
    }

    @PostMapping("/save")
    public ResponseEntity<ProductSummary> saveProduct(@RequestBody ProductRequest request) {

        ProductEntity productEntity = productSyncService.saveProduct(
                ProductEntity.builder()
                        .sku(request.getSku())
                        .name(request.getName())
                        .categoryId(request.getCategoryId())
                        .brand(request.getBrand())
                        .price(request.getPrice())
                        .build()
        );

        return ResponseEntity.ok(new ProductSummary(
                productEntity.getId(),
                productEntity.getSku(),
                productEntity.getName(),
                productEntity.getBrand(),
                productEntity.getCategoryId(),
                productEntity.getPrice()
        ));
    }

}

@NoArgsConstructor
@Setter
@Getter
class ProductRequest {
    private String name;
    private String sku;
    private String brand;
    private Integer categoryId;
    private Integer price;
}