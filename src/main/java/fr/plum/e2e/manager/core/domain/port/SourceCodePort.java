package fr.plum.e2e.manager.core.domain.port;

import fr.plum.e2e.manager.core.domain.model.aggregate.environment.vo.SourceCodeInformation;
import fr.plum.e2e.manager.core.domain.model.aggregate.synchronization.vo.SourceCodeProject;

public interface SourceCodePort {
  SourceCodeProject cloneRepository(SourceCodeInformation sourceCodeInformation);
}
