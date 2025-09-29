package tech.gate.step.searchsystemevolution.domain.product;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface ProductRepository extends JpaRepository<ProductEntity, Long> {




    // 실험 1: 풀스캔 LIKE 검색
    Page<ProductEntity> findByDescriptionContaining(String keyword, Pageable pageable);

    // 실험 2: 단일 인덱스 검색
    Page<ProductEntity> findByBrand(String brand, Pageable pageable);  // <-- List → Page로 고치는게 일관적임

    // 실험 3: 복합 인덱스 검색
    Page<ProductEntity> findByBrandAndCategoryId(String brand, Integer categoryId, Pageable pageable);

    // 고유값 검색 (인덱스 효과 확인용)
    Page<ProductEntity> findBySku(String sku, Pageable pageable);
}
