package fr.plum.e2e.manager.core.domain.model.aggregate.testresult;

import fr.plum.e2e.manager.core.domain.model.aggregate.testresult.vo.TestResultScreenshotId;
import fr.plum.e2e.manager.core.domain.model.aggregate.testresult.vo.TestResultScreenshotTitle;
import fr.plum.e2e.manager.sharedkernel.domain.model.aggregate.Entity;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter
public class TestResultScreenshot extends Entity<TestResultScreenshotId> {

  private TestResultScreenshotTitle title;
  private byte[] screenshot;
}
