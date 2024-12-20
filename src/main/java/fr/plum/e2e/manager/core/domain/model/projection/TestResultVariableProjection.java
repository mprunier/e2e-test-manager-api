package fr.plum.e2e.manager.core.domain.model.projection;

import lombok.Builder;

@Builder
public record TestResultVariableProjection(String name, String value) {}
