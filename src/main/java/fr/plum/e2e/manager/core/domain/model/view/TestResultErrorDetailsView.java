package fr.plum.e2e.manager.core.domain.model.view;

import lombok.Builder;

@Builder
public record TestResultErrorDetailsView(
    String errorMessage, String errorStacktrace, String code) {}
