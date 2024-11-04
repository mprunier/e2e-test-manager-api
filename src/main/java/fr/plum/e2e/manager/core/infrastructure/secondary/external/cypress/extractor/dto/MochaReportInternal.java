package fr.plum.e2e.manager.core.infrastructure.secondary.external.cypress.extractor.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class MochaReportInternal {

  private List<MochaReportResultInternal> results;
}
