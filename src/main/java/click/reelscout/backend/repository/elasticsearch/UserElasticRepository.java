package click.reelscout.backend.repository.elasticsearch;

import click.reelscout.backend.model.elasticsearch.UserDoc;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Elasticsearch repository for User documents.
 * Used for indexing and searching user data in Elasticsearch in optimized way.
 */
public interface UserElasticRepository extends ElasticsearchRepository<UserDoc, Long> {
}
