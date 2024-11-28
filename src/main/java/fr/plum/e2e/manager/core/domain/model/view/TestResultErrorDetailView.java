package fr.plum.e2e.manager.core.domain.model.view;

import lombok.Builder;

@Builder
public record TestResultErrorDetailView(String errorMessage, String errorStacktrace, String code) {}
