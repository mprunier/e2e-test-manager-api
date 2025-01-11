package fr.plum.e2e.manager.core.infrastructure.primary.rest.dto.response;

import fr.plum.e2e.manager.core.domain.model.projection.SearchCriteriaProjection;
import java.util.List;

public record SearchCriteriaResponse(
    List<CriteriaOptionResponse> suites,
    List<CriteriaOptionResponse> tests,
    List<CriteriaOptionResponse> files,
    List<CriteriaOptionResponse> tags) {
  public static SearchCriteriaResponse fromDomain(SearchCriteriaProjection domain) {
    return new SearchCriteriaResponse(
        CriteriaOptionResponse.fromDomain(domain.suites()),
        CriteriaOptionResponse.fromDomain(domain.tests()),
        CriteriaOptionResponse.fromDomain(domain.files()),
        CriteriaOptionResponse.fromDomain(domain.tags()));
  }
}
