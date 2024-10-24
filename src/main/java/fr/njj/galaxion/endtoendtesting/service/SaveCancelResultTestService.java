package fr.njj.galaxion.endtoendtesting.service;

import fr.njj.galaxion.endtoendtesting.domain.enumeration.ConfigurationStatus;
import fr.njj.galaxion.endtoendtesting.model.entity.ConfigurationTestEntity;
import fr.njj.galaxion.endtoendtesting.model.entity.PipelineEntity;
import fr.njj.galaxion.endtoendtesting.model.entity.TestEntity;
import fr.njj.galaxion.endtoendtesting.service.retrieval.ConfigurationTestRetrievalService;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApplicationScoped
@RequiredArgsConstructor
public class SaveCancelResultTestService {

  private final ConfigurationTestRetrievalService configurationTestRetrievalService;

  public void saveTestResult(
      PipelineEntity pipeline, ConfigurationStatus status, String errorMessage) {
    if (pipeline.hasTestIdsFilter()) {
      saveByConfigurationTestIds(pipeline, status, errorMessage);
    } else if (pipeline.hasFilesFilter()) {
      saveByFiles(pipeline, status, errorMessage);
    }
  }

  private void saveByConfigurationTestIds(
      PipelineEntity pipeline, ConfigurationStatus status, String errorMessage) {
    var configurationTestIdsToCancel =
        pipeline.getConfigurationTestIdsFilter().stream().map(Long::valueOf).toList();
    configurationTestIdsToCancel.forEach(
        configurationTestId -> {
          var configurationTestOptional =
              configurationTestRetrievalService.getOptional(configurationTestId);
          configurationTestOptional.ifPresent(
              configurationTest -> saveTest(pipeline, status, errorMessage, configurationTest));
        });
  }

  private void saveByFiles(
      PipelineEntity pipeline, ConfigurationStatus status, String errorMessage) {
    var configurationTestsToCancel =
        configurationTestRetrievalService.getAllByFiles(pipeline.getFilesFilter());
    configurationTestsToCancel.forEach(
        configurationTest -> {
          saveTest(pipeline, status, errorMessage, configurationTest);
        });
  }

  private static void saveTest(
      PipelineEntity pipeline,
      ConfigurationStatus status,
      String errorMessage,
      ConfigurationTestEntity configurationTest) {
    TestEntity.builder()
        .configurationTest(configurationTest)
        .status(status)
        .variables(pipeline.getVariables())
        .createdBy(pipeline.getCreatedBy())
        .errorMessage(errorMessage)
        .build()
        .persist();
  }
}
