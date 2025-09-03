package click.reelscout.backend.model.elasticsearch;

import click.reelscout.backend.model.jpa.ProductionCompany;
import lombok.*;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@ToString(callSuper = true)
@Data
public class ProductionCompanyDoc extends UserDoc {
    @Field(type = FieldType.Search_As_You_Type)
    private String name;

    public ProductionCompanyDoc(ProductionCompany productionCompany) {
        super(productionCompany);
        this.name = productionCompany.getName();
    }
}
