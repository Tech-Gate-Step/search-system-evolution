package tech.gate.step.searchsystemevolution.service.dto;

import java.util.List;

public record ProductPagePayload(
        List<ProductSummary> items,
        long totalCount,
        int page,
        int size
) {
}
