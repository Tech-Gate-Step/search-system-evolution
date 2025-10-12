package tech.gate.step.searchsystemevolution.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tech.gate.step.searchsystemevolution.service.ProductService;
import tech.gate.step.searchsystemevolution.service.dto.ProductSummary;

import java.util.concurrent.ThreadLocalRandom;

@Slf4j
@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    private final static int TOTAL_PAGE = (int) Math.ceil((double) 10_000_000 / 20);

    @GetMapping("/db")
    public ResponseEntity<Page<ProductSummary>> getProducts() {
        int randomPage = ThreadLocalRandom.current().nextInt(TOTAL_PAGE);

        Page<ProductSummary> productSummaries = productService.searchProductsNoCache(null, null, randomPage, 20);
        return ResponseEntity.ok(productSummaries);
    }

    @GetMapping("/cache")
    public ResponseEntity<Page<ProductSummary>> getProductsCache() throws JsonProcessingException {

        int randomPage = ThreadLocalRandom.current().nextInt(TOTAL_PAGE);

        Page<ProductSummary> productSummaries = productService.searchProductsCache(randomPage, 20);
        return ResponseEntity.ok(productSummaries);
    }


}
