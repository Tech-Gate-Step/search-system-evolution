package tech.gate.step.searchsystemevolution.es.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchPage;
import org.springframework.data.web.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tech.gate.step.searchsystemevolution.es.domain.AutoCompleteResult;
import tech.gate.step.searchsystemevolution.es.domain.Product;
import tech.gate.step.searchsystemevolution.es.service.ProductQueryService;
import tech.gate.step.searchsystemevolution.es.service.ProductSyncService;
import tech.gate.step.searchsystemevolution.es.service.SearchAutoCompleteService;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/search/products")
public class ProductSearchController {

    private final ProductQueryService productQueryService;
    private final ProductSyncService productSyncService;
    private final SearchAutoCompleteService searchAutoCompleteService;

    @GetMapping("/list")
    public ResponseEntity<PagedModel<SearchHit<Product>>> listProducts(@RequestParam String keyword,
                                                                       @RequestParam(required = false) String brand,
                                                                       @RequestParam(required = false) Integer minPrice,
                                                                       @RequestParam(required = false) Integer maxPrice,
                                                                       @RequestParam(defaultValue = "relevance") String sortBy,
                                                                       @RequestParam(defaultValue = "0") int page,
                                                                       @RequestParam(defaultValue = "10") int size) {

        SearchPage<Product> searchHits = productQueryService.searchProducts(keyword, brand, minPrice, maxPrice, sortBy, page, size);
        return ResponseEntity.ok(new PagedModel<>(searchHits));
    }

    @PostMapping("/sync")
    public String syncProducts() {

        productSyncService.syncAllProducts();

        return "상품 동기화 작업이 시작되었습니다.";
    }

    @GetMapping("/autocomplete")
    public AutoCompleteResult autocomplete(@RequestParam String keyword) {

        return searchAutoCompleteService.autoComplete(keyword, 10);
    }
}
