package click.reelscout.backend.repository.elasticsearch;

import click.reelscout.backend.model.elasticsearch.UserDoc;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface UserElasticRepository extends ElasticsearchRepository<UserDoc, Long> {
}
