package fr.njj.galaxion.endtoendtesting.domain.internal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class ConfigurationInternal {

    @Builder.Default
    @Setter
    private List<ConfigurationTestInternal> tests = new ArrayList<>();

    @Builder.Default
    @Setter
    private List<ConfigurationSuiteInternal> suites = new ArrayList<>();
}
