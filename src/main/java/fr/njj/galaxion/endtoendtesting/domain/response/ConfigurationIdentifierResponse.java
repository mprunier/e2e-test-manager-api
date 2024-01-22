package fr.njj.galaxion.endtoendtesting.domain.response;

import fr.njj.galaxion.endtoendtesting.domain.enumeration.ConfigurationStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.ZonedDateTime;
import java.util.Set;

@ToString
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class ConfigurationIdentifierResponse {

    private Long id;

    private String identifier;

    private ConfigurationStatus status;

    private Long testId;

    private String testTitle;

    private String suiteTitle;

    private String path;

    @ToString.Exclude
    private Set<String> variables;

    private ZonedDateTime lastPlayedAt;
}
