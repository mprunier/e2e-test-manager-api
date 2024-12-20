package fr.plum.e2e.manager.core.domain.model.projection;

import fr.plum.e2e.manager.core.domain.model.aggregate.testconfiguration.ConfigurationStatus;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;
import lombok.Builder;

@Builder
public record ConfigurationSuiteProjection(
    UUID id,
    String title,
    String file,
    ConfigurationStatus status,
    List<String> variables,
    List<String> tags,
    List<ConfigurationTestProjection> tests,
    ZonedDateTime lastPlayedAt,
    boolean hasNewTest,
    String group) {}
