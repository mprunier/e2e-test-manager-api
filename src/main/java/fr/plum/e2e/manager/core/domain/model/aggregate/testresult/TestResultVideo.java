package fr.plum.e2e.manager.core.domain.model.aggregate.testresult;

import fr.plum.e2e.manager.core.domain.model.aggregate.testresult.vo.TestResultVideoId;
import fr.plum.e2e.manager.sharedkernel.domain.model.aggregate.Entity;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter
public class TestResultVideo extends Entity<TestResultVideoId> {

  private byte[] video;
}
