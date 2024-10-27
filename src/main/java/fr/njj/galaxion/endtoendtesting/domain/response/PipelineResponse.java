package fr.njj.galaxion.endtoendtesting.domain.response;

import fr.njj.galaxion.endtoendtesting.domain.enumeration.PipelineStatus;
import java.util.List;
import lombok.Builder;

@Builder
public record PipelineResponse(
    String id, PipelineStatus status, String statusDescription, List<String> filesFilter) {}
