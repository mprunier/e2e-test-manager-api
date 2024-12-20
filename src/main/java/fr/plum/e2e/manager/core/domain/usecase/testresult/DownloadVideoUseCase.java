package fr.plum.e2e.manager.core.domain.usecase.testresult;

import fr.plum.e2e.manager.core.domain.model.query.DownloadVideoQuery;
import fr.plum.e2e.manager.core.domain.port.out.query.GetTestResultPort;
import fr.plum.e2e.manager.sharedkernel.domain.port.in.QueryUseCase;

public class DownloadVideoUseCase implements QueryUseCase<DownloadVideoQuery, byte[]> {

  private final GetTestResultPort getTestResultPort;

  public DownloadVideoUseCase(GetTestResultPort getTestResultPort) {
    this.getTestResultPort = getTestResultPort;
  }

  public byte[] execute(DownloadVideoQuery query) {
    return getTestResultPort.findVideo(query.testResultVideoId());
  }
}
