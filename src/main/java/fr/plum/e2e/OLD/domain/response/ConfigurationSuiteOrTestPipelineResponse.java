package fr.plum.e2e.OLD.domain.response;

import java.time.ZonedDateTime;
import lombok.Builder;

@Builder
public record ConfigurationSuiteOrTestPipelineResponse(
    String id, ZonedDateTime createdAt, String createdBy, Boolean isAllTests) {}
