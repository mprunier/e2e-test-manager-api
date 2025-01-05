package fr.plum.e2e.manager.core.infrastructure.secondary.external.webclient.cypress.extractor.dto;

import java.util.HashMap;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class ArtifactDataInternal {

  @Builder.Default private Map<String, byte[]> videos = new HashMap<>();

  @Builder.Default private Map<String, byte[]> screenshots = new HashMap<>();

  private MochaReportInternal report;
}
