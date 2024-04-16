package fr.njj.galaxion.endtoendtesting.domain.response;

import fr.njj.galaxion.endtoendtesting.domain.enumeration.ConfigurationStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

@ToString
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class ConfigurationSuiteResponse {

    private Long id;

    private String title;

    private String file;

    private ConfigurationStatus status;

    @ToString.Exclude
    private List<String> variables;

    @Builder.Default
    private List<ConfigurationTestResponse> tests = new ArrayList<>();

    private ZonedDateTime lastPlayedAt;
    
    private boolean hasNewTest;
}
