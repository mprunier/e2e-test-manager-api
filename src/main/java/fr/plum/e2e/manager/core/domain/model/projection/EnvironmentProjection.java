package fr.plum.e2e.manager.core.domain.model.projection;

import java.util.UUID;
import lombok.Builder;

@Builder
public record EnvironmentProjection(UUID id, String description) {}
