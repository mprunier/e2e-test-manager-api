package fr.plum.e2e.manager.core.application.query.testresult;

import fr.plum.e2e.manager.core.domain.model.query.DownloadScreenshotQuery;
import fr.plum.e2e.manager.core.domain.port.projection.GetTestResultPort;
import fr.plum.e2e.manager.sharedkernel.application.query.QueryHandler;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class DownloadScreenshotQueryHandler
    implements QueryHandler<DownloadScreenshotQuery, byte[]> {

  private final GetTestResultPort getTestResultPort;

  public DownloadScreenshotQueryHandler(GetTestResultPort getTestResultPort) {
    this.getTestResultPort = getTestResultPort;
  }

  public byte[] execute(DownloadScreenshotQuery query) {
    var screenshot = getTestResultPort.findScreenshot(query.testResultScreenshotId());
    if (screenshot == null) {
      throw new RuntimeException("Screenshot not found");
    }
    return screenshot;
  }
}
