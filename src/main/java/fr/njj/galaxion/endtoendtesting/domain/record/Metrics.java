package fr.njj.galaxion.endtoendtesting.domain.record;

import java.time.ZonedDateTime;
import lombok.Builder;

@Builder
public record Metrics(
    boolean isAllTestsRun,
    ZonedDateTime at,
    int suites,
    int tests,
    int passPercent,
    int passes,
    int failures,
    int skipped) {}
