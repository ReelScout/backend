package click.reelscout.backend.controller;

import click.reelscout.backend.dto.response.ContentResponseDTO;
import click.reelscout.backend.service.definition.ContentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("${api.paths.content}")
public class ContentController {
    private final ContentService contentService;

    @GetMapping("/all")
    public ResponseEntity<List<ContentResponseDTO>> all() {
        return ResponseEntity.ok(contentService.getAll());
    }

    @GetMapping("/content-types")
    public ResponseEntity<List<String>> contentTypes() {
        return ResponseEntity.ok(contentService.getContentTypes());
    }
}
