package fr.plum.e2e.manager.core.infrastructure.primary.rest.dto.response;

import lombok.Builder;

@Builder
public record TestResultErrorDetails(String errorMessage, String errorStacktrace, String code) {}
