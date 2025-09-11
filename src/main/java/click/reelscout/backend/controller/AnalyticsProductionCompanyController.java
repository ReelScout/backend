package click.reelscout.backend.controller;

import click.reelscout.backend.dto.response.analytics.ProductionDashboardDTO;
import click.reelscout.backend.model.jpa.ProductionCompany;
import click.reelscout.backend.service.definition.AnalyticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("${api.paths.content}/analytics")
@RestController
@PreAuthorize("hasRole(T(click.reelscout.backend.model.jpa.Role).PRODUCTION_COMPANY)")
public class AnalyticsProductionCompanyController {

    private final AnalyticsService analyticsService;

    @GetMapping("/dashboard")
    public ResponseEntity<ProductionDashboardDTO> getDashboard(@AuthenticationPrincipal ProductionCompany authenticatedProduction) {
        return ResponseEntity.ok(analyticsService.getProductionDashboard(authenticatedProduction));
    }
}

