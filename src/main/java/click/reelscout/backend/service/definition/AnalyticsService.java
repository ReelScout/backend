package click.reelscout.backend.service.definition;

import click.reelscout.backend.dto.response.analytics.ProductionDashboardDTO;
import click.reelscout.backend.model.jpa.ProductionCompany;

public interface AnalyticsService {
    ProductionDashboardDTO getProductionDashboard(ProductionCompany productionCompany);
}

