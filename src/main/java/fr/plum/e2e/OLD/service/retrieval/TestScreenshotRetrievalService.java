package fr.plum.e2e.OLD.service.retrieval;

import fr.plum.e2e.OLD.domain.exception.TestScreenshotNotFoundException;
import fr.plum.e2e.OLD.model.repository.TestScreenshotRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApplicationScoped
@RequiredArgsConstructor
public class TestScreenshotRetrievalService {

  private final TestScreenshotRepository testScreenshotRepository;

  @Transactional
  public byte[] getScreenshot(Long id) {
    return testScreenshotRepository
        .findByIdOptional(id)
        .orElseThrow(() -> new TestScreenshotNotFoundException(id))
        .getScreenshot();
  }
}
