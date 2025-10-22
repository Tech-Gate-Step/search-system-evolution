package tech.gate.step.searchsystemevolution.es.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.gate.step.searchsystemevolution.domain.product.ProductEntity;
import tech.gate.step.searchsystemevolution.domain.product.ProductRepository;
import tech.gate.step.searchsystemevolution.es.domain.Product;
import tech.gate.step.searchsystemevolution.es.event.ProductCreatedEvent;
import tech.gate.step.searchsystemevolution.es.repository.ProductElasticRepository;

import java.util.List;

@Slf4j
@Service(value = "productSyncService")
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ProductSyncService {

    private final ProductRepository productRepository;  // MariaDB
    private final ProductElasticRepository elasticRepository;
    private final ApplicationEventPublisher applicationEventPublisher;


    @Transactional
    public ProductEntity saveProduct(ProductEntity productEntity) {

        ProductEntity save = productRepository.save(productEntity);

        applicationEventPublisher.publishEvent(new ProductCreatedEvent(save.getId()));


        return save;
    }

    /**
     * MariaDB의 모든 상품을 Elasticsearch로 동기화
     */
    @Transactional(readOnly = true)
    public void syncAllProducts() {
        log.info("=== 전체 상품 동기화 시작 ===");

        int pageSize = 1000;
        int pageNumber = 0;
        long totalSynced = 0;

        while (true) {
            PageRequest pageable = PageRequest.of(pageNumber, pageSize);
            Page<ProductEntity> productPage = productRepository.findAll(pageable);

            if (productPage.isEmpty()) {
                break;
            }

            List<Product> documents = productPage.getContent().stream()
                    .map(Product::from)
                    .toList();

            elasticRepository.saveAll(documents);
            totalSynced += documents.size();

            log.info("진행: {}/{} 동기화 완료", totalSynced, productPage.getTotalElements());

            if (!productPage.hasNext()) {
                break;
            }
            pageNumber++;
        }

        log.info("=== 전체 동기화 완료: {} 건 ===", totalSynced);
    }

}
