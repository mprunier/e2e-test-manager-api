package fr.plum.e2e.manager.core.domain.model.view;

import java.util.UUID;
import lombok.Builder;

@Builder
public record EnvironmentView(UUID id, String description) {}
