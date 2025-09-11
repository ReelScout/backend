package click.reelscout.backend.model.elasticsearch;

import click.reelscout.backend.model.jpa.User;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

/**
 * Abstract Elasticsearch document for User entity.
 */
@ToString
@Document(indexName = "users")
@NoArgsConstructor
@Data
public abstract class UserDoc {
    @Id
    private Long id;

    @Field(type = FieldType.Search_As_You_Type)
    private String username;

    @Field(type = FieldType.Search_As_You_Type)
    private String email;

    protected UserDoc(User user) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.email = user.getEmail();
    }
}
