package fr.plum.e2e.manager.core.infrastructure.primary.rest.dto.response;

import fr.plum.e2e.manager.core.domain.model.view.SearchCriteriaView;
import java.util.List;

public record SearchCriteriaResponse(
    List<CriteriaOptionResponse> suites,
    List<CriteriaOptionResponse> tests,
    List<CriteriaOptionResponse> files,
    List<CriteriaOptionResponse> tags) {
  public static SearchCriteriaResponse fromDomain(SearchCriteriaView domain) {
    return new SearchCriteriaResponse(
        CriteriaOptionResponse.fromDomain(domain.suites()),
        CriteriaOptionResponse.fromDomain(domain.tests()),
        CriteriaOptionResponse.fromDomain(domain.files()),
        CriteriaOptionResponse.fromDomain(domain.tags()));
  }
}
