package tech.gate.step.searchsystemevolution.util;


import org.springframework.data.domain.Page;

import java.util.List;
import java.util.function.Supplier;

public class PerformanceMeasureUtil {

    public PerformanceMeasureUtil() {}

    public static <T> void measure(String label, Supplier<T> action) {
        long start = System.currentTimeMillis();
        T result = action.get();
        long end = System.currentTimeMillis();
        long elapsed = end - start;

        // 결과 타입별 출력
        if (result instanceof Page<?> page) {
            System.out.printf(
                    "==============================%n" +
                            "▶ 테스트 유형   : %s%n" +
                            "▶ 페이지 건수   : %d건 (이번 조회)%n" +
                            "▶ 전체 건수     : %d건 (DB 매칭 총합)%n" +
                            "▶ 총 소요 시간 : %.3f 초%n" +
                            "==============================%n",
                    label, page.getNumberOfElements(), page.getTotalElements(), elapsed / 1000.0
            );
        } else if (result instanceof List<?> list) {
            System.out.printf(
                    "==============================%n" +
                            "▶ 테스트 유형   : %s%n" +
                            "▶ 결과 건수     : %d건%n" +
                            "▶ 총 소요 시간 : %.3f 초%n" +
                            "==============================%n",
                    label, list.size(), elapsed / 1000.0
            );
        } else if (result instanceof Number number) {
            System.out.printf(
                    "==============================%n" +
                            "▶ 테스트 유형   : %s%n" +
                            "▶ 결과 건수     : %d건%n" +
                            "▶ 총 소요 시간 : %.3f 초%n" +
                            "==============================%n",
                    label, number.longValue(), elapsed / 1000.0
            );
        } else {
            System.out.printf(
                    "==============================%n" +
                            "▶ 테스트 유형   : %s%n" +
                            "▶ 총 소요 시간 : %.3f 초%n" +
                            "==============================%n",
                    label, elapsed / 1000.0
            );
        }
    }
}
