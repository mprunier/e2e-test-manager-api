package fr.plum.e2e.manager.core.infrastructure.secondary.external.webclient.gitlab.domain.request;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Singular;
import lombok.ToString;

@ToString
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class PipelineRequest {

  private String ref;

  @Singular private List<VariableRequest> variables;
}
