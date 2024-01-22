package fr.njj.galaxion.endtoendtesting.domain.request;

import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class RunTestOrSuiteRequest {

  private Long configurationTestId;

  private Long configurationSuiteId;

  @Builder.Default private List<TestVariableRequest> variables = new ArrayList<>();
}
