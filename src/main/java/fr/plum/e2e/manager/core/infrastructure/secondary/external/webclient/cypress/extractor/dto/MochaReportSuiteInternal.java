package fr.plum.e2e.manager.core.infrastructure.secondary.external.webclient.cypress.extractor.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class MochaReportSuiteInternal {

  private String title;
  private List<MochaReportTestInternal> tests;
  private List<MochaReportSuiteInternal> suites;
  private Integer duration;
}
