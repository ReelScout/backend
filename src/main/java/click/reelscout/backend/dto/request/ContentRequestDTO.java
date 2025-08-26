package click.reelscout.backend.dto.request;

import click.reelscout.backend.model.Actor;
import click.reelscout.backend.model.ContentType;
import click.reelscout.backend.model.Director;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class ContentRequestDTO {
    @NotBlank(message = "Title is mandatory")
    private String title;

    @NotBlank(message = "Description is mandatory")
    private String description;

    @NotNull(message = "Content type is mandatory")
    private ContentType contentType;

    @NotEmpty(message = "There has to be at least one actor")
    private List<Actor> actors;

    @NotEmpty(message = "There has to be at least one director")
    private List<Director> directors;

    private String base64Image;

    private String trailerUrl;
}
