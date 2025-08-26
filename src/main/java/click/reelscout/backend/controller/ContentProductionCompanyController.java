package click.reelscout.backend.controller;

import click.reelscout.backend.dto.request.ContentRequestDTO;
import click.reelscout.backend.dto.response.ContentResponseDTO;
import click.reelscout.backend.model.ProductionCompany;
import click.reelscout.backend.service.definition.ContentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RequestMapping("${api.paths.content}")
@RestController
@PreAuthorize("hasRole(T(click.reelscout.backend.model.Role).PRODUCTION_COMPANY)")
public class ContentProductionCompanyController {
    private final ContentService contentService;

    @PostMapping("/add")
    public ResponseEntity<ContentResponseDTO> addContent(@AuthenticationPrincipal ProductionCompany authenticatedProduction, @Valid @RequestBody ContentRequestDTO contentRequestDTO) {
        return ResponseEntity.ok(contentService.create(authenticatedProduction, contentRequestDTO));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<ContentResponseDTO> updateContent(@AuthenticationPrincipal ProductionCompany authenticatedProduction, @PathVariable Long id, @Valid @RequestBody ContentRequestDTO contentRequestDTO) {
        return ResponseEntity.ok(contentService.update(authenticatedProduction, id, contentRequestDTO));
    }
}