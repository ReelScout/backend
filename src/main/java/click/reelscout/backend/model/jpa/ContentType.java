package click.reelscout.backend.model.jpa;

import com.fasterxml.jackson.annotation.JsonValue;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Entity
public class ContentType implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @JsonValue
    @Id
    private String name;
}
