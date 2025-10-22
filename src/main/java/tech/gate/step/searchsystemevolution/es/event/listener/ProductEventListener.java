package tech.gate.step.searchsystemevolution.es.event.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import tech.gate.step.searchsystemevolution.domain.product.ProductEntity;
import tech.gate.step.searchsystemevolution.domain.product.ProductRepository;
import tech.gate.step.searchsystemevolution.es.domain.Product;
import tech.gate.step.searchsystemevolution.es.event.ProductCreatedEvent;
import tech.gate.step.searchsystemevolution.es.repository.ProductElasticRepository;

@Component
@Slf4j
@RequiredArgsConstructor
public class ProductEventListener {

    private final ProductRepository productRepository;
    private final ProductElasticRepository productElasticRepository;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onProductCreated(ProductCreatedEvent event) {
        ProductEntity productEntity = productRepository.findById(event.productId()).orElseThrow(() -> new IllegalArgumentException("Invalid product ID: " + event.productId()));
        Product product = Product.from(productEntity);

        productElasticRepository.save(product);
        log.info("Elasticsearch에 상품 저장 완료: ID={}", product.getId());
    }

}
