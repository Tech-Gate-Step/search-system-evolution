package tech.gate.step.searchsystemevolution.util;


import org.springframework.data.domain.Page;

import java.util.function.Supplier;

public class PerformanceMeasureUtil {

    public PerformanceMeasureUtil() {
    }

    public static <T> void measure(String label, Supplier<T> action) {
        long start = System.currentTimeMillis();
        T result = action.get();
        long end = System.currentTimeMillis();
        double elapsed = (end - start) / 1000.0;

        // 결과 타입별 출력
        if (result instanceof Page<?> page) {
            System.out.printf(
                    "==============================%n" +
                            "▶ 테스트 유형   : %s%n" +
                            "▶ 페이지 건수   : %d건 (이번 조회)%n" +
                            "▶ 전체 건수     : %d건 (DB 매칭 총합)%n" +
                            "▶ 총 소요 시간 : %.3f 초%n" +
                            "==============================%n",
                    label, page.getNumberOfElements(), page.getTotalElements(), elapsed
            );
            return;
        }

        if (result instanceof java.util.List<?> list) {
            System.out.printf(
                    "==============================%n" +
                            "▶ 테스트 유형   : %s%n" +
                            "▶ 결과 건수     : %d건%n" +
                            "▶ 총 소요 시간 : %.3f 초%n" +
                            "==============================%n",
                    label, list.size(), elapsed
            );
            return;
        }

        if (result instanceof Number n) {
            System.out.printf(
                    "==============================%n" +
                            "▶ 테스트 유형   : %s%n" +
                            "▶ 결과 건수     : %d건%n" +
                            "▶ 총 소요 시간 : %.3f 초%n" +
                            "==============================%n",
                    label, n.longValue(), elapsed
            );
            return;
        }

        System.out.printf(
                "==============================%n" +
                        "▶ 테스트 유형   : %s%n" +
                        "▶ 총 소요 시간 : %.3f 초%n" +
                        "==============================%n",
                label, elapsed
        );
    }
}
