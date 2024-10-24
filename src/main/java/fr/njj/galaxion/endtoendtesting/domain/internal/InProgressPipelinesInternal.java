package fr.njj.galaxion.endtoendtesting.domain.internal;

import java.util.List;
import java.util.Map;
import lombok.Builder;

// L'un ou l'autre
@Builder
public record InProgressPipelinesInternal(
    Map<Long, List<PipelineDetailsInternal>> pipelinesByConfigurationTestId, boolean isAllTests) {}
