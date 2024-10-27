package fr.njj.galaxion.endtoendtesting.domain.internal;

import fr.njj.galaxion.endtoendtesting.domain.exception.SuiteShouldBeNotContainsSubSuiteException;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class ConfigurationSuiteInternal {

  private String title;

  @Builder.Default private List<String> variables = new ArrayList<>();

  @Builder.Default private List<String> tags = new ArrayList<>();

  @Builder.Default private List<ConfigurationTestInternal> tests = new ArrayList<>();

  @Builder.Default private List<ConfigurationSuiteInternal> suites = new ArrayList<>();

  @Builder.Default private boolean toDisable = false;

  public void assertNotExistSubSuite() {
    if (!suites.isEmpty()) {
      throw new SuiteShouldBeNotContainsSubSuiteException();
    }
  }
}
