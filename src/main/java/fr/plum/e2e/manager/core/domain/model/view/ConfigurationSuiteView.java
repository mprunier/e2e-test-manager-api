package fr.plum.e2e.manager.core.domain.model.view;

import fr.plum.e2e.manager.core.domain.model.aggregate.testconfiguration.ConfigurationStatus;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;
import lombok.Builder;

@Builder
public record ConfigurationSuiteView(
    UUID id,
    String title,
    String file,
    ConfigurationStatus status,
    List<String> variables,
    List<String> tags,
    List<ConfigurationTestView> tests,
    ZonedDateTime lastPlayedAt,
    boolean hasNewTest,
    String group) {}
