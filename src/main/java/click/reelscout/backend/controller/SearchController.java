package click.reelscout.backend.controller;

import click.reelscout.backend.dto.response.SearchResponseDTO;
import click.reelscout.backend.dto.response.UserResponseDTO;
import click.reelscout.backend.service.definition.SearchService;
import jakarta.persistence.Cacheable;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@Cacheable
@RequestMapping("${api.paths.search}")
@RestController
public class SearchController<S extends UserResponseDTO> {
    private final SearchService<S> searchService;

    @GetMapping
    public ResponseEntity<SearchResponseDTO<S>> search(@RequestParam String query) {
        return ResponseEntity.ok(searchService.search(query));
    }

    @GetMapping("/members")
    public ResponseEntity<List<S>> searchMembers(@RequestParam String query) {
        return ResponseEntity.ok(searchService.searchMembers(query));
    }
}
