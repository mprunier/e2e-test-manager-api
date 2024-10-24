package fr.njj.galaxion.endtoendtesting.domain.response;

import fr.njj.galaxion.endtoendtesting.domain.enumeration.PipelineStatus;
import lombok.Builder;

@Builder
public record PipelineResponse(String id, PipelineStatus status) {}
