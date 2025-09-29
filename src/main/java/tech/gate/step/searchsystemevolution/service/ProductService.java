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

import java.time.Duration;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final RedisTemplate<String, Object> redisTemplate;

    /**
     * Redis 캐시를 사용하는 검색
     */
    @SuppressWarnings("unchecked")
    public Page<ProductEntity> searchProductsWithCache(String brand, Integer categoryId, int page) {
        // 1. 캐시 키 생성
        String cacheKey = String.format("products:%s:%d:%d", brand, categoryId, page);

        // 2. 캐시 확인
        Object cached = redisTemplate.opsForValue().get(cacheKey);
        if (cached != null) {
            log.info("캐시 HIT: {}", cacheKey);
            // 캐시에선 List<ProductEntity> 꺼내서 PageImpl로 감싸줌
            return new PageImpl<>((List<ProductEntity>) cached, PageRequest.of(page, 500_000),
                    ((List<ProductEntity>) cached).size());
        }

        log.info("캐시 MISS: {} - DB 조회 시작", cacheKey);

        // 3. DB 조회
        Page<ProductEntity> result = productRepository.findByBrandAndCategoryId(
                brand, categoryId, PageRequest.of(page, 500_000));

        // 4. 캐시 저장 (Page 전체가 아니라 content만 저장)
        redisTemplate.opsForValue().set(cacheKey, result.getContent(), Duration.ofMinutes(10));
        log.info("캐시 저장 완료: {}", cacheKey);

        return result;
    }

    /**
     * 캐시 없이 DB만 사용하는 검색 (비교용)
     */
    public Page<ProductEntity> searchProductsNoCache(String brand, Integer categoryId, int page) {
        return productRepository.findByBrandAndCategoryId(
                brand, categoryId, PageRequest.of(page, 500_000));
    }

    /**
     * 특정 브랜드의 캐시 전체 삭제 (캐시 무효화)
     */
    public void clearCache(String brand) {
        String pattern = "products:" + brand + ":*";
        redisTemplate.keys(pattern).forEach(key -> {
            redisTemplate.delete(key);
            log.info("캐시 삭제: {}", key);
        });
    }

    /**
     * 전체 캐시 삭제
     */
    public void clearAllCache() {
        String pattern = "products:*";
        redisTemplate.keys(pattern).forEach(redisTemplate::delete);
        log.info("전체 캐시 삭제 완료");
    }
}
