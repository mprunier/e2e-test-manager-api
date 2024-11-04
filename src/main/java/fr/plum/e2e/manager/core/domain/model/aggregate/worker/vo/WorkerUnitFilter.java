package fr.plum.e2e.manager.core.domain.model.aggregate.worker.vo;

import fr.plum.e2e.manager.core.domain.model.aggregate.testconfiguration.SuiteConfiguration;
import fr.plum.e2e.manager.core.domain.model.aggregate.testconfiguration.TestConfiguration;
import fr.plum.e2e.manager.core.domain.model.aggregate.testconfiguration.vo.FileName;
import fr.plum.e2e.manager.core.domain.model.aggregate.testconfiguration.vo.Tag;
import java.util.ArrayList;
import java.util.List;
import lombok.Builder;

@Builder
public record WorkerUnitFilter(
    List<FileName> fileNames,
    Tag tag,
    SuiteConfiguration suiteConfiguration,
    TestConfiguration testConfiguration) {

  public WorkerUnitFilter {
    if (fileNames == null) {
      fileNames = new ArrayList<>();
    }
  }

  // only if only testTitle exist
  public boolean canRecordVideo() {
    return (fileNames.isEmpty() || fileNames.size() == 1)
        && suiteConfiguration == null
        && testConfiguration != null;
  }
}
