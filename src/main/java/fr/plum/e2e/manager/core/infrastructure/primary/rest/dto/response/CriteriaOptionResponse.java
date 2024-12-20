package fr.plum.e2e.manager.core.infrastructure.primary.rest.dto.response;

import fr.plum.e2e.manager.core.domain.model.projection.CriteriaOptionProjection;
import jakarta.validation.constraints.NotBlank;
import java.util.List;

public record CriteriaOptionResponse(@NotBlank String value, @NotBlank String label) {
  public static CriteriaOptionResponse fromDomain(CriteriaOptionProjection domain) {
    return new CriteriaOptionResponse(domain.value(), domain.label());
  }

  public static List<CriteriaOptionResponse> fromDomain(List<CriteriaOptionProjection> domains) {
    return domains.stream().map(CriteriaOptionResponse::fromDomain).toList();
  }
}
