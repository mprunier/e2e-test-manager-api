package fr.njj.galaxion.endtoendtesting.service;

import fr.njj.galaxion.endtoendtesting.domain.enumeration.ConfigurationStatus;
import fr.njj.galaxion.endtoendtesting.model.entity.ConfigurationTestEntity;
import fr.njj.galaxion.endtoendtesting.model.entity.PipelineEntity;
import fr.njj.galaxion.endtoendtesting.model.entity.TestEntity;
import fr.njj.galaxion.endtoendtesting.service.retrieval.ConfigurationTestRetrievalService;
import jakarta.enterprise.context.ApplicationScoped;
import java.time.ZonedDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApplicationScoped
@RequiredArgsConstructor
public class SaveCancelResultTestService {

  private final ConfigurationTestRetrievalService configurationTestRetrievalService;

  public void saveTestResult(
      PipelineEntity pipeline,
      ConfigurationStatus status,
      String errorMessage,
      boolean isSaveImmediately) {
    if (pipeline.hasTestIdsFilter()) {
      saveByConfigurationTestIds(pipeline, status, errorMessage, isSaveImmediately);
    } else if (pipeline.hasFilesFilter()) {
      saveByFiles(pipeline, status, errorMessage, isSaveImmediately);
    }
  }

  private void saveByConfigurationTestIds(
      PipelineEntity pipeline,
      ConfigurationStatus status,
      String errorMessage,
      boolean isSaveImmediatly) {
    var configurationTestIdsToCancel =
        pipeline.getConfigurationTestIdsFilter().stream().map(Long::valueOf).toList();
    configurationTestIdsToCancel.forEach(
        configurationTestId -> {
          var configurationTestOptional =
              configurationTestRetrievalService.getOptional(configurationTestId);
          configurationTestOptional.ifPresent(
              configurationTest ->
                  saveTest(pipeline, status, errorMessage, configurationTest, isSaveImmediatly));
        });
  }

  private void saveByFiles(
      PipelineEntity pipeline,
      ConfigurationStatus status,
      String errorMessage,
      boolean isSaveImmediately) {
    var configurationTestsToCancel =
        configurationTestRetrievalService.getAllByFiles(pipeline.getFilesFilter());
    configurationTestsToCancel.forEach(
        configurationTest -> {
          saveTest(pipeline, status, errorMessage, configurationTest, isSaveImmediately);
        });
  }

  private static void saveTest(
      PipelineEntity pipeline,
      ConfigurationStatus status,
      String errorMessage,
      ConfigurationTestEntity configurationTest,
      boolean isSaveImmediately) {
    TestEntity.builder()
        .configurationTest(configurationTest)
        .isWaiting(!isSaveImmediately)
        .status(status)
        .variables(pipeline.getVariables())
        .createdBy(pipeline.getCreatedBy())
        .errorMessage(errorMessage)
        .createdAt(ZonedDateTime.now())
        .build()
        .persist();
  }
}
