package tech.gate.step.searchsystemevolution;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import tech.gate.step.searchsystemevolution.domain.product.ProductEntity;
import tech.gate.step.searchsystemevolution.domain.product.ProductRepository;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class SearchSystemEvolutionApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProductRepository productRepository;

    @BeforeEach
    void setUp() {
        // 테스트 데이터 준비
        ProductEntity product1 = new ProductEntity(null, "LG OLED TV", 1, 1200.00, "A bright and clear OLED TV", "LG");
        ProductEntity product2 = new ProductEntity(null, "Samsung QLED TV", 1, 1100.00, "A colorful QLED TV", "Samsung");
        productRepository.save(product1);
        productRepository.save(product2);
    }

    @Test
    @DisplayName("Description에 포함된 키워드로 상품을 검색하면, 해당 상품이 반환되어야 한다.")
    void searchProductsByDescriptionTest() throws Exception {
        // given
        String keyword = "OLED";

        // when & then
        mockMvc.perform(get("/products/search").param("keyword", keyword))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].description", containsString(keyword)))
                .andDo(print());
    }

    @Test
    void contextLoads() {
    }

}
