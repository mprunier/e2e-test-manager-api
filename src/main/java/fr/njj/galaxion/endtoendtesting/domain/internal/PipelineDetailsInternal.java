package fr.njj.galaxion.endtoendtesting.domain.internal;

import java.time.ZonedDateTime;
import lombok.Builder;

@Builder
public record PipelineDetailsInternal(String id, ZonedDateTime createdAt, String createdBy) {}
