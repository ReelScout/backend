package click.reelscout.backend.model.elasticsearch;

import click.reelscout.backend.model.jpa.Content;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

/**
 * Elasticsearch document for Content entity.
 */
@NoArgsConstructor
@Data
@Document(indexName = "contents")
public class ContentDoc {
    @Id
    private Long id;

    @Field(type = FieldType.Search_As_You_Type)
    private String title;

    @Field(type = FieldType.Search_As_You_Type)
    private String description;

    public ContentDoc(Content content) {
        this.id = content.getId();
        this.title = content.getTitle();
        this.description = content.getDescription();
    }
}
