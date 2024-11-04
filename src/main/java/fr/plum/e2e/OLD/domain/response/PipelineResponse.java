package fr.plum.e2e.OLD.domain.response;

import fr.plum.e2e.manager.core.infrastructure.primary.rest.dto.enumeration.WorkerStatus;
import java.util.List;
import lombok.Builder;

@Builder
public record PipelineResponse(
    String id, WorkerStatus status, String statusDescription, List<String> filesFilter) {}
