package tech.gate.step.searchsystemevolution.service.dto;

public record ProductSummary(
        Long id,
        String sku,
        String name,
        String brand,
        Integer categoryId,
        Integer price
) {
}
