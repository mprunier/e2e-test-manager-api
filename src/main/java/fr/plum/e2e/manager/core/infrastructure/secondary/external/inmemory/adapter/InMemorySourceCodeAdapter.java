package fr.plum.e2e.manager.core.infrastructure.secondary.external.inmemory.adapter;

import fr.plum.e2e.manager.core.domain.model.aggregate.environment.vo.SourceCodeInformation;
import fr.plum.e2e.manager.core.domain.model.aggregate.synchronization.vo.SourceCodeProject;
import fr.plum.e2e.manager.core.domain.port.SourceCodePort;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class InMemorySourceCodeAdapter implements SourceCodePort {

  private final Map<String, SourceCodeProject> clonedRepositories = new HashMap<>();

  @Override
  public SourceCodeProject cloneRepository(SourceCodeInformation sourceCodeInformation) {
    var key = buildKey(sourceCodeInformation);
    return clonedRepositories.computeIfAbsent(
        key, k -> new SourceCodeProject(new File("/tmp/test-" + key)));
  }

  private String buildKey(SourceCodeInformation info) {
    return String.format("%s-%s", info.projectId(), info.branch());
  }
}
