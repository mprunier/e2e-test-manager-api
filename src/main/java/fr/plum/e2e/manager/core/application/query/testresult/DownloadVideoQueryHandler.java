package fr.plum.e2e.manager.core.application.query.testresult;

import fr.plum.e2e.manager.core.domain.model.query.DownloadVideoQuery;
import fr.plum.e2e.manager.core.domain.port.out.view.GetTestResultPort;
import fr.plum.e2e.manager.sharedkernel.domain.port.in.QueryHandler;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class DownloadVideoQueryHandler implements QueryHandler<DownloadVideoQuery, byte[]> {

  private final GetTestResultPort getTestResultPort;

  public DownloadVideoQueryHandler(GetTestResultPort getTestResultPort) {
    this.getTestResultPort = getTestResultPort;
  }

  public byte[] execute(DownloadVideoQuery query) {
    return getTestResultPort.findVideo(query.testResultVideoId());
  }
}
