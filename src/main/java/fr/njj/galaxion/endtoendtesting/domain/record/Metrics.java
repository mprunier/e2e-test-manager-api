package fr.njj.galaxion.endtoendtesting.domain.record;

import lombok.Builder;

import java.time.ZonedDateTime;

@Builder
public record Metrics(
        ZonedDateTime at,
        int suites,
        int tests,
        int passPercent,
        int passes,
        int failures,
        int skipped
) {
}