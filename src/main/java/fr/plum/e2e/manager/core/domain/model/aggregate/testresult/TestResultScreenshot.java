package fr.plum.e2e.manager.core.domain.model.aggregate.testresult;

import fr.plum.e2e.manager.core.domain.model.aggregate.testresult.vo.TestResultScreenshotId;
import fr.plum.e2e.manager.core.domain.model.aggregate.testresult.vo.TestResultScreenshotTitle;
import fr.plum.e2e.manager.sharedkernel.domain.assertion.Assert;
import fr.plum.e2e.manager.sharedkernel.domain.model.aggregate.Entity;
import lombok.Builder;
import lombok.Getter;

@Getter
public class TestResultScreenshot extends Entity<TestResultScreenshotId> {

  private TestResultScreenshotTitle title;
  private byte[] screenshot;

  @Builder
  public TestResultScreenshot(
      TestResultScreenshotId testResultScreenshotId,
      byte[] screenshot,
      TestResultScreenshotTitle title) {
    super(testResultScreenshotId);
    Assert.notNull("screenshot", screenshot);
    Assert.notNull("title", title);
    this.screenshot = screenshot;
    this.title = title;
  }

  public static TestResultScreenshot create(TestResultScreenshotTitle title, byte[] screenshot) {
    return builder()
        .testResultScreenshotId(TestResultScreenshotId.generate())
        .screenshot(screenshot)
        .title(title)
        .build();
  }
}
