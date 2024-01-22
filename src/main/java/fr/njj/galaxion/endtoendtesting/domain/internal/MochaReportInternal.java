package fr.njj.galaxion.endtoendtesting.domain.internal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class MochaReportInternal {

    private MochaReportStatsInternal stats;
    private List<MochaReportResultInternal> results;
}
