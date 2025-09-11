package click.reelscout.backend.model.elasticsearch;

import click.reelscout.backend.model.jpa.Member;
import lombok.*;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

/**
 * Elasticsearch document for Member entity, extending UserDoc.
 */
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@NoArgsConstructor
@Data
public class MemberDoc extends UserDoc {
    @Field(type = FieldType.Search_As_You_Type)
    private String firstName;

    @Field(type = FieldType.Search_As_You_Type)
    private String lastName;

    public MemberDoc(Member member) {
        super(member);
        this.firstName = member.getFirstName();
        this.lastName = member.getLastName();
    }
}
