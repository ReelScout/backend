package click.reelscout.backend.repository.elasticsearch;

import click.reelscout.backend.model.elasticsearch.ContentDoc;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface ContentElasticRepository extends ElasticsearchRepository<ContentDoc, Long> {
}
