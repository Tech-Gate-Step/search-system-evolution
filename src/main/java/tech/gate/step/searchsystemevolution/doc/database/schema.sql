CREATE TABLE products
(
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    sku         VARCHAR(20)  NOT NULL UNIQUE, -- SKU 코드
    name        VARCHAR(255) NOT NULL,        -- 상품명
    description TEXT,                         -- 설명
    brand       VARCHAR(100),                 -- 브랜드명
    category_id INT,                          -- 카테고리 ID
    price       INT,                          -- 가격
    stock       INT,                          -- 재고 수량
    rating      DECIMAL(2, 1),                -- 평점 (1.0~5.0)
    sales_count INT,                          -- 판매량
    created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 인덱스: 조회 성능 개선용
CREATE INDEX idx_products_category ON products(category_id);
CREATE INDEX idx_products_sales ON products(sales_count DESC);
CREATE INDEX idx_products_price ON products(price);
