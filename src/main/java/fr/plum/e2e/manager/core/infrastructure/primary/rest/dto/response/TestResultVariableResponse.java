package fr.plum.e2e.manager.core.infrastructure.primary.rest.dto.response;

import lombok.Builder;

@Builder
public record TestResultVariableResponse(String name, String value) {}
