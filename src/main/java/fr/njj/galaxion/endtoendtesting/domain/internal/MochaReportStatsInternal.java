package fr.njj.galaxion.endtoendtesting.domain.internal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class MochaReportStatsInternal {

    private Integer suites;
    private Integer tests;
    private Integer passes;
    private Integer pending;
    private Integer failures;
    private Integer skipped;
    private Integer other;
    private Integer passPercent;
}
