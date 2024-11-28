package fr.plum.e2e.manager.core.infrastructure.primary.rest.dto.response;

import java.util.UUID;
import lombok.Builder;

@Builder
public record TestResultScreenshotResponse(UUID id, String name) {}
