package tech.gate.step.searchsystemevolution.domain.product;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProductRepository extends JpaRepository<ProductEntity, Long> {




    // 실험 1: 풀스캔 LIKE 검색
    Page<ProductEntity> findByDescriptionContaining(String keyword, Pageable pageable);

    // 실험 2-1: 네이티브:brand 검색 (인덱스 무시)
    @Query(
            value = "SELECT * " +
                    "FROM products IGNORE INDEX (idx_products_brand) " +
                    "WHERE brand = :brand",

            countQuery = "SELECT count(*) " +
                         "FROM products IGNORE INDEX (idx_products_brand) " +
                         "WHERE brand = :brand",
            nativeQuery = true
    )
    Page<ProductEntity> findByBrandIgnoreIndex(@Param("brand") String brand, Pageable pageable);

    // 실험 2-2: 단일 인덱스 검색
    Page<ProductEntity> findByBrand(String brand, Pageable pageable);

    // 실험 3: 복합 인덱스 검색
    Page<ProductEntity> findByBrandAndCategoryId(String brand, Integer categoryId, Pageable pageable);

}
