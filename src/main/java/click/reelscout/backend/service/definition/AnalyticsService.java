package click.reelscout.backend.service.definition;

import click.reelscout.backend.dto.response.analytics.ProductionDashboardDTO;
import click.reelscout.backend.model.jpa.ProductionCompany;

/**
 * Service definition for production-related analytics.
 * <p>
 * Provides methods to compute dashboards and KPIs for a production company.
 */
public interface AnalyticsService {
    /**
     * Build a dashboard containing KPIs and charts for the given production company.
     *
     * @param productionCompany the production company to compute analytics for
     * @return {@link ProductionDashboardDTO} with aggregated analytics
     */
    ProductionDashboardDTO getProductionDashboard(ProductionCompany productionCompany);
}
