package fr.njj.galaxion.endtoendtesting.domain.response;

import java.time.ZonedDateTime;
import lombok.Builder;

@Builder
public record PipelineDetailsResponse(
    String id, ZonedDateTime createdAt, String createdBy, boolean isAllTests) {}
