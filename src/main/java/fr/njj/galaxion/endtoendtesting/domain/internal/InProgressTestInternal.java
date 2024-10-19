package fr.njj.galaxion.endtoendtesting.domain.internal;

import java.util.Map;
import lombok.Builder;

@Builder
public record InProgressTestInternal(
    Map<Long, Integer> numberOfTestInProgressById, boolean isAllTestsInProgress) {}
