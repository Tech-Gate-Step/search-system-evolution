package tech.gate.step.searchsystemevolution.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import tech.gate.step.searchsystemevolution.domain.product.ProductEntity;
import tech.gate.step.searchsystemevolution.domain.product.ProductRepository;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Transactional
@ActiveProfiles("local")
class ProductServiceTest {


    @Autowired
    private ProductRepository productRepository;

    @Test
    void 키워드_검색_시간_테스트() {

        // given
        String keyword = "OLED";

//        List<ProductEntity> entity = productRepository.findByDescriptionContaining(keyword);
//        List<ProductEntity> all = productRepository.findAll();
        long startMs = System.currentTimeMillis();
        Pageable limit = PageRequest.of(0, 500000);
        long endMs = System.currentTimeMillis();
        productRepository.findAll(limit).getContent();
        System.out.println((startMs - endMs) + "ms");

    }

    @Test
    void contextLoads() {
    }
}
