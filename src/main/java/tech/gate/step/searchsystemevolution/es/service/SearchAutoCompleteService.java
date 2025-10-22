package tech.gate.step.searchsystemevolution.es.service;

import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.stereotype.Service;
import tech.gate.step.searchsystemevolution.es.domain.AutoCompleteResult;
import tech.gate.step.searchsystemevolution.es.domain.Product;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class SearchAutoCompleteService {

    private final ElasticsearchOperations elasticsearchOperations;

    public AutoCompleteResult autoComplete(String keyword, int limit) {

        if(keyword == null || keyword.isEmpty()) {
            return AutoCompleteResult.builder()
                    .suggestions(List.of())
                    .build();
        }

        Query productNameQuery = Query.of(q -> q
                .match(m -> m
                        .field("name.autocomplete")
                        .query(keyword)
                        .boost(3.0f) //상품명 가중치 높게
                ));

        BoolQuery bool = BoolQuery.of(b -> b
                .should(productNameQuery)
                .minimumShouldMatch("1"));


        NativeQuery searchQuery = NativeQuery.builder()
                .withQuery(Query.of(q -> q.bool(bool)))
                .withPageable(PageRequest.of(0, limit))
                .build();

        SearchHits<Product> searchHits = elasticsearchOperations.search(searchQuery, Product.class);

        List<AutoCompleteResult.Suggestion> suggestions = searchHits.getSearchHits().stream()
                .map(hit -> {
                    Product product = hit.getContent();
                    String matchedText = product.getName();
                    String type = "product";

                    return AutoCompleteResult.Suggestion.builder()
                            .text(matchedText)
                            .type(type)
                            .productId(product.getId())
                            .score(hit.getScore())
                            .build();
                })
                .distinct()
                .limit(limit).collect(Collectors.toList());
        return AutoCompleteResult.builder().suggestions(suggestions).build();
    }

}
