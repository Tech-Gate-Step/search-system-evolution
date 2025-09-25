package tech.gate.step.searchsystemevolution.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tech.gate.step.searchsystemevolution.domain.product.ProductEntity;
import tech.gate.step.searchsystemevolution.domain.product.ProductRepository;

import java.util.List;

import static tech.gate.step.searchsystemevolution.util.testLogger.log;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    public void searchProductsByDescription(String keyword) {
        long startMs = System.currentTimeMillis();
        List<ProductEntity> entity = productRepository.findByDescriptionContaining(keyword);
        long endMs = System.currentTimeMillis();

        log("[RDBMS] 데이터 " + entity.size() + "건 조회 : ms = " + (endMs - startMs));

    }
}
