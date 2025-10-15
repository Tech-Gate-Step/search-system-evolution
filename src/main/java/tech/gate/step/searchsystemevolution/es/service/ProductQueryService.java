package tech.gate.step.searchsystemevolution.es.service;

import co.elastic.clients.elasticsearch._types.SortOptions;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch._types.query_dsl.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHitSupport;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.SearchPage;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.gate.step.searchsystemevolution.es.domain.Product;

import java.util.ArrayList;
import java.util.List;

@Service(value = "productQueryService")
@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ProductQueryService {

    private final ElasticsearchOperations elasticsearchOperations;

    // 상품 조회 관련 메서드 구현
    public SearchPage<Product> searchProducts(String keyword, String brand, Integer minPrice, Integer maxPrice, String sortBy, int page, int size) {

        // Bool Query 구성
        List<Query> mustQueries = new ArrayList<>();
        List<Query> filterQueries = new ArrayList<>();


        // 1. 키워드 검색 (name, description에서 검색)
        if (keyword != null && !keyword.isBlank()) {
            MultiMatchQuery multiMatch = MultiMatchQuery.of(m -> m
                    .query(keyword)
                    .fields("name^3", "description^1", "brand^2")  // 가중치
                    .fuzziness("AUTO")  // 오타 허용
                    .operator(Operator.And)
            );
            mustQueries.add(Query.of(q -> q.multiMatch(multiMatch)));
        }

        // 2. 브랜드 필터
        if (brand != null && !brand.isBlank()) {
            TermQuery brandQuery = TermQuery.of(t -> t
                    .field("brand")
                    .value(brand)
            );
            filterQueries.add(Query.of(q -> q.term(brandQuery)));
        }

        // 올바른 방법 1: 람다 체이닝
        if (minPrice != null || maxPrice != null) {
            RangeQuery priceQuery = new RangeQuery.Builder()
                    .number(p -> {
                        p.field("price");
                        if (minPrice != null) {
                            p.gte((double) minPrice);
                        }
                        if (maxPrice != null) {
                            p.lte((double) maxPrice);
                        }
                        return p;
                    })
                    .build();
            filterQueries.add(priceQuery._toQuery());
        }

        // Bool Query 생성
        BoolQuery boolQuery = BoolQuery.of(b -> {
            b.must(mustQueries);
            b.filter(filterQueries);
            return b;
        });

        // 정렬
        List<SortOptions> sortOptions = new ArrayList<>();
        if (sortBy != null) {
            switch (sortBy) {
                case "price_asc" -> sortOptions.add(SortOptions.of(s -> s
                        .field(f -> f.field("price").order(SortOrder.Asc))));
                case "price_desc" -> sortOptions.add(SortOptions.of(s -> s
                        .field(f -> f.field("price").order(SortOrder.Desc))));
                case "rating" -> sortOptions.add(SortOptions.of(s -> s
                        .field(f -> f.field("rating").order(SortOrder.Desc))));
                case "sales" -> sortOptions.add(SortOptions.of(s -> s
                        .field(f -> f.field("salesCount").order(SortOrder.Desc))));
            }

        }
        // 기본 정렬: 관련도 순
        sortOptions.add(SortOptions.of(s -> s.score(sc -> sc.order(SortOrder.Desc))));

        // 쿼리 실행
        NativeQuery searchQuery = NativeQuery.builder()
                .withQuery(Query.of(q -> q.bool(boolQuery)))
                .withSort(sortOptions)
                .withPageable(PageRequest.of(page, size))
                .build();

        SearchHits<Product> searchHits = elasticsearchOperations.search(
                searchQuery,
                Product.class
        );
        SearchPage<Product> productPage = SearchHitSupport.searchPageFor(searchHits, searchQuery.getPageable());

        // ProductDocument -> ProductSummary 변환
        return productPage;
    }

}
