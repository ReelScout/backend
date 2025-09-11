package click.reelscout.backend.controller;

import click.reelscout.backend.dto.response.ContentResponseDTO;
import click.reelscout.backend.service.definition.ContentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * REST controller for retrieving available content, content types, and genres.
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("${api.paths.content}")
public class ContentController {
    private final ContentService contentService;

    /**
     * Retrieves all available content as a list of {@link ContentResponseDTO}.
     *
     * @return a {@link ResponseEntity} containing the list of content
     */
    @GetMapping("/all")
    public ResponseEntity<List<ContentResponseDTO>> all() {
        return ResponseEntity.ok(contentService.getAll());
    }

    /**
     * Retrieves the list of available content types.
     *
     * @return a {@link ResponseEntity} containing the list of content types
     */
    @GetMapping("/content-types")
    public ResponseEntity<List<String>> contentTypes() {
        return ResponseEntity.ok(contentService.getContentTypes());
    }

    /**
     * Retrieves the list of available genres.
     *
     * @return a {@link ResponseEntity} containing the list of genres
     */
    @GetMapping("/genres")
    public ResponseEntity<List<String>> genres() {
        return ResponseEntity.ok(contentService.getGenres());
    }
}
