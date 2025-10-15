package tech.gate.step.searchsystemevolution.es.repository;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import tech.gate.step.searchsystemevolution.es.domain.Product;

public interface ProductElasticRepository extends ElasticsearchRepository<Product, Long>, ProductSearchRepositoryCustom {
}
