package fr.plum.e2e.manager.core.domain.model.aggregate.synchronization.vo;

import fr.plum.e2e.manager.sharedkernel.domain.assertion.Assert;
import java.io.File;

public record SourceCodeProject(File project) {
  public SourceCodeProject {
    Assert.notNull("File", project);
  }
}
