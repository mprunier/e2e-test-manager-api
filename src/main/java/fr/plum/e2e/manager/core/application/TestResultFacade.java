package fr.plum.e2e.manager.core.application;

import fr.plum.e2e.manager.core.domain.model.query.DownloadScreenshotQuery;
import fr.plum.e2e.manager.core.domain.model.query.DownloadVideoQuery;
import fr.plum.e2e.manager.core.domain.model.query.GetAllTestResultQuery;
import fr.plum.e2e.manager.core.domain.model.query.GetTestResultErrorDetailQuery;
import fr.plum.e2e.manager.core.domain.model.view.TestResultErrorDetailView;
import fr.plum.e2e.manager.core.domain.model.view.TestResultView;
import fr.plum.e2e.manager.core.domain.port.out.query.GetTestResultPort;
import fr.plum.e2e.manager.core.domain.usecase.testresult.DownloadScreenshotUseCase;
import fr.plum.e2e.manager.core.domain.usecase.testresult.DownloadVideoUseCase;
import fr.plum.e2e.manager.core.domain.usecase.testresult.GetAllTestResultUseCase;
import fr.plum.e2e.manager.core.domain.usecase.testresult.GetTestResultErrorDetailUseCase;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;

@ApplicationScoped
public class TestResultFacade {

  private final GetAllTestResultUseCase getAllTestResultUseCase;
  private final GetTestResultErrorDetailUseCase getTestResultErrorDetailUseCase;
  private final DownloadScreenshotUseCase downloadScreenshotUseCase;
  private final DownloadVideoUseCase downloadVideoUseCase;

  public TestResultFacade(GetTestResultPort getTestResultPort) {
    this.getAllTestResultUseCase = new GetAllTestResultUseCase(getTestResultPort);
    this.getTestResultErrorDetailUseCase = new GetTestResultErrorDetailUseCase(getTestResultPort);
    this.downloadScreenshotUseCase = new DownloadScreenshotUseCase(getTestResultPort);
    this.downloadVideoUseCase = new DownloadVideoUseCase(getTestResultPort);
  }

  public List<TestResultView> getAllTestResult(GetAllTestResultQuery query) {
    return getAllTestResultUseCase.execute(query);
  }

  public TestResultErrorDetailView getTestResultErrorDetail(GetTestResultErrorDetailQuery query) {
    return getTestResultErrorDetailUseCase.execute(query);
  }

  public byte[] downloadScreenshot(DownloadScreenshotQuery query) {
    return downloadScreenshotUseCase.execute(query);
  }

  public byte[] downloadVideo(DownloadVideoQuery query) {
    return downloadVideoUseCase.execute(query);
  }
}
