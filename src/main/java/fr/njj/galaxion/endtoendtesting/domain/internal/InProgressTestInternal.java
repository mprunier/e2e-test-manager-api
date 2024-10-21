package fr.njj.galaxion.endtoendtesting.domain.internal;

import java.util.List;
import java.util.Map;
import lombok.Builder;

@Builder
public record InProgressTestInternal(
    Map<Long, List<PipelineDetailsInternal>> pipelinesByConfigurationTestId,
    String allTestsPipelineId) {}
