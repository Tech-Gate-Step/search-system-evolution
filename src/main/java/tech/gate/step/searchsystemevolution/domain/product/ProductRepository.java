package tech.gate.step.searchsystemevolution.domain.product;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;


public interface ProductRepository extends JpaRepository<ProductEntity, Long> {

    List<ProductEntity> findByCategoryId(int categoryId);

    @Query("SELECT p FROM ProductEntity p WHERE p.description LIKE %:keyword%")
    List<ProductEntity> findByDescriptionContaining(@Param("keyword") String keyword);
}
