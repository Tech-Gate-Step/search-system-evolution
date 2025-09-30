package tech.gate.step.searchsystemevolution.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import tech.gate.step.searchsystemevolution.domain.product.ProductEntity;
import tech.gate.step.searchsystemevolution.domain.product.ProductRepository;
import tech.gate.step.searchsystemevolution.service.dto.ProductPagePayload;
import tech.gate.step.searchsystemevolution.service.dto.ProductSummary;

import java.time.Duration;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final RedisTemplate<String, Object> redisTemplate;

    /**
     * Redis 캐시를 사용하는 검색
     */
    public Page<ProductSummary> searchProductsWithCache(String brand, Integer categoryId, int page, int size) {
        String key = "products:%s:%d:%d:%d".formatted(brand, categoryId, page, size);

        // 1. 캐시 조회
        ProductPagePayload cached = (ProductPagePayload) redisTemplate.opsForValue().get(key);
        if (cached != null) {
            log.info("캐시 HIT: {}", key);
            return new PageImpl<>(cached.items(), PageRequest.of(page, size), cached.totalCount());
        }

        log.info("캐시 MISS: {} - DB 조회", key);

        // 2. DB 조회
        PageRequest pageable = PageRequest.of(page, size);
        Page<ProductEntity> p = productRepository.findByBrandAndCategoryId(brand, categoryId, pageable);

        List<ProductSummary> items = p.getContent().stream()
                .map(e -> new ProductSummary(
                        e.getId(),
                        e.getSku(),
                        e.getName(),
                        e.getBrand(),
                        e.getCategoryId(),
                        e.getPrice()
                ))
                .toList();

        ProductPagePayload payload = new ProductPagePayload(items, p.getTotalElements(), page, size);

        // 3. 캐시에 저장 (TTL 10분)
        redisTemplate.opsForValue().set(key, payload, Duration.ofMinutes(10));
        log.info("캐시 저장 완료: {}", key);

        return new PageImpl<>(items, pageable, p.getTotalElements());
    }

    /**
     * 비교용: 캐시 없이 DB 만
     */
    public Page<ProductSummary> searchProductsNoCache(String brand, Integer categoryId, int page, int size) {
        PageRequest pageable = PageRequest.of(page, size);
        Page<ProductEntity> p = productRepository.findByBrandAndCategoryId(brand, categoryId, pageable);

        List<ProductSummary> items = p.getContent().stream()
                .map(e -> new ProductSummary(
                        e.getId(),
                        e.getSku(),
                        e.getName(),
                        e.getBrand(),
                        e.getCategoryId(),
                        e.getPrice()
                ))
                .toList();

        return new PageImpl<>(items, pageable, p.getTotalElements());
    }

    /**
     * 무효화 (테스트/실험용)
     */
    public void clearCache(String brand) {
        Set<String> keys = redisTemplate.keys("products:" + brand + ":*");
        keys.forEach(redisTemplate::delete);
    }

    /**
     * 전체 캐시 삭제
     */
    public void clearAllCache() {
        var keys = redisTemplate.keys("products:*");
        keys.forEach(redisTemplate::delete);

    }
}
