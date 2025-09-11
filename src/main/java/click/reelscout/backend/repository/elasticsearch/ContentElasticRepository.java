package click.reelscout.backend.repository.elasticsearch;

import click.reelscout.backend.model.elasticsearch.ContentDoc;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Elasticsearch repository for Content documents.
 * Used for indexing and searching content data in Elasticsearch in optimized way.
 */
public interface ContentElasticRepository extends ElasticsearchRepository<ContentDoc, Long> {
}
