package fr.njj.galaxion.endtoendtesting.domain.internal;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class MochaReportInternal {

  private MochaReportStatsInternal stats;
  private List<MochaReportResultInternal> results;
}
