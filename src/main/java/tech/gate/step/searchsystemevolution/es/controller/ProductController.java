package tech.gate.step.searchsystemevolution.es.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tech.gate.step.searchsystemevolution.service.dto.ProductSummary;

@RestController
@RequestMapping("/api/v1/p")
public class ProductController {

    @PostMapping
    public ResponseEntity<ProductSummary> createProduct() {
        return ResponseEntity.ok(null);
    }

}
