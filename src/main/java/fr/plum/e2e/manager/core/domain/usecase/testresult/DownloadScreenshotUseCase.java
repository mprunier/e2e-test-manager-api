package fr.plum.e2e.manager.core.domain.usecase.testresult;

import fr.plum.e2e.manager.core.domain.model.query.DownloadScreenshotQuery;
import fr.plum.e2e.manager.core.domain.port.out.query.GetTestResultPort;
import fr.plum.e2e.manager.sharedkernel.domain.port.in.QueryUseCase;

public class DownloadScreenshotUseCase implements QueryUseCase<DownloadScreenshotQuery, byte[]> {

  private final GetTestResultPort getTestResultPort;

  public DownloadScreenshotUseCase(GetTestResultPort getTestResultPort) {
    this.getTestResultPort = getTestResultPort;
  }

  public byte[] execute(DownloadScreenshotQuery query) {
    return getTestResultPort.findScreenshot(query.testResultScreenshotId());
  }
}
