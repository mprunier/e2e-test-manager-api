package fr.plum.e2e.manager.core.domain.model.aggregate.testresult;

import fr.plum.e2e.manager.core.domain.model.aggregate.testresult.vo.TestResultVideoId;
import fr.plum.e2e.manager.sharedkernel.domain.assertion.Assert;
import fr.plum.e2e.manager.sharedkernel.domain.model.aggregate.Entity;
import lombok.Builder;
import lombok.Getter;

@Getter
public class TestResultVideo extends Entity<TestResultVideoId> {

  private byte[] video;

  @Builder
  public TestResultVideo(TestResultVideoId testResultVideoId, byte[] video) {
    super(testResultVideoId);
    Assert.notNull("video", video);
    this.video = video;
  }

  public static TestResultVideo create(byte[] video) {
    return builder().testResultVideoId(TestResultVideoId.generate()).video(video).build();
  }
}
