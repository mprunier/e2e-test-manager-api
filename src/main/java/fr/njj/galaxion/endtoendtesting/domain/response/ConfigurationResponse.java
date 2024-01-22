package fr.njj.galaxion.endtoendtesting.domain.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@ToString
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class ConfigurationResponse {

    private Long file;

    private List<ConfigurationSuiteResponse> suites;
}
