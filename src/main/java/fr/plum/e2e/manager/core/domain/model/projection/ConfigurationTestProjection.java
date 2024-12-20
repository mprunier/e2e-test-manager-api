package fr.plum.e2e.manager.core.domain.model.projection;

import fr.plum.e2e.manager.core.domain.model.aggregate.testconfiguration.ConfigurationStatus;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;
import lombok.Builder;

@Builder
public record ConfigurationTestProjection(
    UUID id,
    String title,
    ConfigurationStatus status,
    List<String> variables,
    List<String> tags,
    ZonedDateTime lastPlayedAt) {}
