package tech.gate.step.searchsystemevolution.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import tech.gate.step.searchsystemevolution.service.ProductService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;

    @GetMapping("/search")
    public void searchProducts(@RequestParam("keyword") String keyword) {
        productService.searchProductsByDescription(keyword);
    }
}
