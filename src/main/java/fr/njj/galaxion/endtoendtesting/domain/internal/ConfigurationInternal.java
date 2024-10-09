package fr.njj.galaxion.endtoendtesting.domain.internal;

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
public class ConfigurationInternal {

  @Builder.Default private List<ConfigurationTestInternal> tests = new ArrayList<>();

  @Builder.Default private List<ConfigurationSuiteInternal> suites = new ArrayList<>();

  private String group;
}
