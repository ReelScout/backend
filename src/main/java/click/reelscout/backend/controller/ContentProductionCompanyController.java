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

/**
 * REST controller providing endpoints for production companies to manage their own content and view analytics.
 * <p>
 * Access is restricted to users with the {@code PRODUCTION_COMPANY} role (enforced by {@link PreAuthorize}).
 */
@RequiredArgsConstructor
@RequestMapping("${api.paths.content}")
@RestController
@PreAuthorize("hasRole(T(click.reelscout.backend.model.jpa.Role).PRODUCTION_COMPANY)")
public class ContentProductionCompanyController {
    private final ContentService contentService;
    private final AnalyticsService analyticsService;

    /**
     * Creates new content.
     *
     * @param authenticatedProduction the authenticated production company
     * @param contentRequestDTO the content data
     * @return the response containing the created content
     */
    @PostMapping("/add")
    public ResponseEntity<ContentResponseDTO> addContent(@AuthenticationPrincipal ProductionCompany authenticatedProduction, @Valid @RequestBody ContentRequestDTO contentRequestDTO) {
        return ResponseEntity.ok(contentService.create(authenticatedProduction, contentRequestDTO));
    }

    /**
     * Updates an existing content.
     *
     * @param authenticatedProduction the authenticated production company
     * @param id the content id
     * @param contentRequestDTO the new content data
     * @return the response containing the updated content
     */
    @PutMapping("/update/{id}")
    public ResponseEntity<ContentResponseDTO> updateContent(@AuthenticationPrincipal ProductionCompany authenticatedProduction, @PathVariable Long id, @Valid @RequestBody ContentRequestDTO contentRequestDTO) {
        return ResponseEntity.ok(contentService.update(authenticatedProduction, id, contentRequestDTO));
    }

    /**
     * Retrieves all contents associated with the authenticated production company.
     *
     * @param authenticatedProduction the authenticated production company
     * @return the response containing a list of contents
     */
    @GetMapping("/my-contents")
    public ResponseEntity<List<ContentResponseDTO>> getMyContents(@AuthenticationPrincipal ProductionCompany authenticatedProduction) {
        return ResponseEntity.ok(contentService.getByProductionCompany(authenticatedProduction));
    }

    /**
     * Deletes the specified content.
     *
     * @param authenticatedProduction the authenticated production company
     * @param id the content id to delete
     * @return the response confirming deletion
     */
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<CustomResponseDTO> deleteContent(@AuthenticationPrincipal ProductionCompany authenticatedProduction, @PathVariable Long id) {
        return ResponseEntity.ok(contentService.delete(authenticatedProduction, id));
    }

    /**
     * Retrieves statistics for the authenticated production company's contents.
     *
     * @param authenticatedProduction the authenticated production company
     * @return the response containing content table row statistics
     */
    @GetMapping("/my-contents/stats")
    public ResponseEntity<List<ContentTableRowDTO>> getMyContentsStats(@AuthenticationPrincipal ProductionCompany authenticatedProduction) {
        var dashboard = analyticsService.getProductionDashboard(authenticatedProduction);
        return ResponseEntity.ok(dashboard.getTable());
    }
}
