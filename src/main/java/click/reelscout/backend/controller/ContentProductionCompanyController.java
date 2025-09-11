package click.reelscout.backend.controller;

import click.reelscout.backend.dto.request.ContentRequestDTO;
import click.reelscout.backend.dto.response.analytics.ContentTableRowDTO;
import click.reelscout.backend.dto.response.ContentResponseDTO;
import click.reelscout.backend.dto.response.CustomResponseDTO;
import click.reelscout.backend.model.jpa.ProductionCompany;
import click.reelscout.backend.service.definition.ContentService;
import click.reelscout.backend.service.definition.AnalyticsService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RequestMapping("${api.paths.content}")
@RestController
@PreAuthorize("hasRole(T(click.reelscout.backend.model.jpa.Role).PRODUCTION_COMPANY)")
public class ContentProductionCompanyController {
    private final ContentService contentService;
    private final AnalyticsService analyticsService;

    @PostMapping("/add")
    public ResponseEntity<ContentResponseDTO> addContent(@AuthenticationPrincipal ProductionCompany authenticatedProduction, @Valid @RequestBody ContentRequestDTO contentRequestDTO) {
        return ResponseEntity.ok(contentService.create(authenticatedProduction, contentRequestDTO));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<ContentResponseDTO> updateContent(@AuthenticationPrincipal ProductionCompany authenticatedProduction, @PathVariable Long id, @Valid @RequestBody ContentRequestDTO contentRequestDTO) {
        return ResponseEntity.ok(contentService.update(authenticatedProduction, id, contentRequestDTO));
    }

    @GetMapping("/my-contents")
    public ResponseEntity<List<ContentResponseDTO>> getMyContents(@AuthenticationPrincipal ProductionCompany authenticatedProduction) {
        return ResponseEntity.ok(contentService.getByProductionCompany(authenticatedProduction));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<CustomResponseDTO> deleteContent(@AuthenticationPrincipal ProductionCompany authenticatedProduction, @PathVariable Long id) {
        return ResponseEntity.ok(contentService.delete(authenticatedProduction, id));
    }

    @GetMapping("/my-contents/stats")
    public ResponseEntity<List<ContentTableRowDTO>> getMyContentsStats(@AuthenticationPrincipal ProductionCompany authenticatedProduction) {
        var dashboard = analyticsService.getProductionDashboard(authenticatedProduction);
        return ResponseEntity.ok(dashboard.getTable());
    }
}
