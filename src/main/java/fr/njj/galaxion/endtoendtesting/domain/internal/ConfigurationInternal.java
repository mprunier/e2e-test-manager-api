package fr.njj.galaxion.endtoendtesting.domain.internal;

import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class ConfigurationInternal {

  @Builder.Default @Setter private List<ConfigurationTestInternal> tests = new ArrayList<>();

  @Builder.Default @Setter private List<ConfigurationSuiteInternal> suites = new ArrayList<>();
}
