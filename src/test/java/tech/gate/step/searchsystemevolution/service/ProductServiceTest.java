package tech.gate.step.searchsystemevolution.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import tech.gate.step.searchsystemevolution.domain.product.ProductRepository;
import tech.gate.step.searchsystemevolution.util.PerformanceMeasureUtil;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


@SpringBootTest
@Transactional
@ActiveProfiles("local")
class ProductServiceTest {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductService productService;

    @BeforeEach
    void setup() {
        // 매 테스트마다 캐시 초기화
        productService.clearAllCache();
    }

    @Test
    @DisplayName("실험1-1: RDB 풀스캔 LIKE 검색 - 삼성")
    void test1_1_RDB_풀스캔_삼성() {
        PerformanceMeasureUtil.measure("RDB 풀스캔 LIKE 검색 (삼성)",
                () -> productRepository.findByDescriptionContaining(
                        "애플", PageRequest.of(0, 500_000)));
    }


    @Test
    @DisplayName("실험1-2: RDB 풀스캔 LIKE 검색 - 애플")
    void test1_2_풀스캔_애플() {
        PerformanceMeasureUtil.measure("RDB 풀스캔 LIKE 검색 (애플)",
                () -> productRepository.findByDescriptionContaining(
                        "삼성", PageRequest.of(0, 500_000)));
    }


    //============================================================================
    // 실험 2: 단일 인덱스
    //============================================================================
    @Test
    @DisplayName("실험2-1: 고유값 검색 -> (인덱스 무시 강제) ")
    void test2_1_인덱스_무시_brand() {
        PerformanceMeasureUtil.measure("RDB 단일 인덱스 검색 (brand = 삼성, 인덱스 무시)",
                () -> productRepository.findByBrandIgnoreIndex(
                        "삼성", PageRequest.of(0, 500_000)));
    }

    @Test
    @DisplayName("실험2-2: 고유값 검색 (인덱스 효과 확인)")
    void test2_2_인덱스_brand() {
        PerformanceMeasureUtil.measure("RDB 단일 인덱스 검색 (brand = 삼성, 인덱스 사용)",
                () -> productRepository.findByBrand(
                        "삼성", PageRequest.of(0, 500_000)));
    }

    //============================================================================
    // 실험 3: 복합 인덱스
    //============================================================================
    @Test
    @DisplayName("실험3: 복합 인덱스 검색")
    void test3_복합인덱스() {
        PerformanceMeasureUtil.measure("RDB 복합 인덱스 (brand + category)",
                () -> productRepository.findByBrandAndCategoryId(
                        "삼성", 3, PageRequest.of(0, 500_000)));
    }

    //============================================================================
    // 실험 4: Redis 캐시
    //============================================================================
    @Test
    @DisplayName("실험4-1: Redis 첫 조회 (캐시 미스)")
    void 실험4_1_캐시_미스() {
        PerformanceMeasureUtil.measure("Redis MISS",
                () -> productService.searchProductsWithCache("삼성", 3, 0, 500_000));
    }

    @Test
    @DisplayName("실험4-2: Redis 재조회 (캐시 히트)")
    void 실험4_2_캐시_히트() {
        // 캐시 저장
        productService.searchProductsWithCache("삼성", 3, 0, 500_000);

        // 실제 측정
        PerformanceMeasureUtil.measure("Redis HIT",
                () -> productService.searchProductsWithCache("삼성", 3, 0, 500_000));
    }

    @Test
    @DisplayName("실험4-3: Redis 캐시 미스 vs 히트 비교")
    void 실험4_3_Redis_비교() {
        System.out.println("\n========== 첫 조회 (캐시 미스) ==========");
        PerformanceMeasureUtil.measure("Redis MISS",
                () -> productService.searchProductsWithCache("삼성", 3, 0, 500_000));

        System.out.println("\n========== 두 번째 조회 (캐시 히트) ==========");
        PerformanceMeasureUtil.measure("Redis HIT",
                () -> productService.searchProductsWithCache("삼성", 3, 0, 500_000));

        System.out.println("\n========== 세 번째 조회 (캐시 히트) ==========");
        PerformanceMeasureUtil.measure("Redis HIT (again)",
                () -> productService.searchProductsWithCache("삼성", 3, 0, 500_000));
    }

    @Test
    @DisplayName("실험5-1: 동시성 테스트 - 캐시 없음")
    void test5_1_동시성_캐시_없음() throws InterruptedException {
        int threadCount = 100;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        // 끝났다고 알려야 할 작업 개수
        CountDownLatch latch = new CountDownLatch(threadCount);

        long start = System.currentTimeMillis();

        for (int i = 0; i < threadCount; i++) {
            executor.submit(() -> {
                try {
                    productService.searchProductsNoCache("삼성", 3, 0, 50);
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        long elapsed = System.currentTimeMillis() - start;

        System.out.printf(
                "%n==============================%n" +
                        "▶ 테스트 유형   : 동시성 (캐시 없음)%n" +
                        "▶ 동시 요청 수  : %d개%n" +
                        "▶ 총 소요 시간  : %.3f초%n" +
                        "▶ 평균 응답시간 : %.3f초%n" +
                        "==============================%n",
                threadCount, elapsed / 1000.0, (elapsed / 1000.0) / threadCount
        );

        executor.shutdown();
    }


    @Test
    @DisplayName("실험5-2: 동시성 테스트 - Redis 캐시")
    void test5_2_동시성_캐시_있음() throws InterruptedException {
        // 1회 워밍: 캐시 채우기
        productService.searchProductsWithCache("삼성", 3, 0, 50);

        int threadCount = 100;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);

        long start = System.currentTimeMillis();

        for (int i = 0; i < threadCount; i++) {
            executor.submit(() -> {
                try {
                    productService.searchProductsWithCache("삼성", 3, 0, 50);
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        long elapsed = System.currentTimeMillis() - start;

        System.out.printf(
                "%n==============================%n" +
                        "▶ 테스트 유형   : 동시성 (Redis 캐시)%n" +
                        "▶ 동시 요청 수  : %d개%n" +
                        "▶ 총 소요 시간  : %.3f초%n" +
                        "▶ 평균 응답시간 : %.3f초%n" +
                        "==============================%n",
                threadCount, elapsed / 1000.0, elapsed / 1000.0 / threadCount
        );

        executor.shutdown();
    }
}
