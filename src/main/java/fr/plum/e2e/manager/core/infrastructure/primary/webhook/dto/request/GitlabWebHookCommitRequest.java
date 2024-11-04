package fr.plum.e2e.manager.core.infrastructure.primary.webhook.dto.request;

import java.util.List;

public record GitlabWebHookCommitRequest(
    List<String> added, List<String> modified, List<String> removed) {}
