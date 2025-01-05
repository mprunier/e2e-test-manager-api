package fr.plum.e2e.manager.core.infrastructure.secondary.external.webclient.cypress.extractor.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class MochaReportErrorInternal {

  private String message;
  private String estack;
}
