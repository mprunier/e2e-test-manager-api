package fr.plum.e2e.OLD.mapper;

import fr.plum.e2e.OLD.domain.response.ScreenshotResponse;
import fr.plum.e2e.OLD.domain.response.TestResponse;
import fr.plum.e2e.OLD.model.entity.ConfigurationTestTagEntity;
import fr.plum.e2e.OLD.model.entity.TestEntity;
import fr.plum.e2e.OLD.model.entity.TestScreenshotEntity;
import fr.plum.e2e.manager.core.infrastructure.primary.rest.dto.response.TestResultVariableResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class TestResponseMapper {

  public static TestResponse buildTestResponseWithDetails(TestEntity entity) {
    var testResponse = buildTestResponse(entity);
    testResponse.setErrorMessage(entity.getErrorMessage());
    testResponse.setErrorStacktrace(entity.getErrorStacktrace());
    testResponse.setCode(entity.getCode());
    return testResponse;
  }

  public static List<ScreenshotResponse> buildScreenshotResponses(TestEntity entity) {
    return entity.getScreenshots().stream()
        .map(TestResponseMapper::buildScreenshotResponse)
        .toList();
  }

  private static ScreenshotResponse buildScreenshotResponse(
      TestScreenshotEntity testScreenshotEntity) {
    return ScreenshotResponse.builder()
        .id(testScreenshotEntity.getId())
        .name(testScreenshotEntity.getFilename())
        .build();
  }

  private static TestResponse buildTestResponse(TestEntity entity) {
    var screenshots = buildScreenshotResponses(entity);
    var configurationTest = entity.getConfigurationTest();
    return TestResponse.builder()
        .id(entity.getId())
        .configurationFileTitle(configurationTest.getFile())
        .configurationSuiteTitle(configurationTest.getConfigurationSuite().getTitle())
        .configurationTestTitle(configurationTest.getTitle())
        .status(entity.getStatus())
        .reference(entity.getReference())
        .errorUrl(entity.getErrorUrl())
        .createdAt(entity.getCreatedAt())
        .createdBy(entity.getCreatedBy())
        .variables(buildTestVariableResponses(entity.getVariables()))
        .configurationTestTags(
            configurationTest.getConfigurationTags() != null
                ? configurationTest.getConfigurationTags().stream()
                    .map(ConfigurationTestTagEntity::getTag)
                    .toList()
                : null)
        .duration(entity.getDuration())
        .screenshots(screenshots)
        .hasVideo(entity.getVideo() != null)
        .build();
  }

  public static List<TestResultVariableResponse> buildTestVariableResponses(
      Map<String, String> entities) {
    var testVariableResponses = new ArrayList<TestResultVariableResponse>();
    entities.forEach(
        (a, b) ->
            testVariableResponses.add(
                TestResultVariableResponse.builder().name(a).value(b).build()));
    return testVariableResponses;
  }

  public static List<TestResponse> buildTestResponses(List<TestEntity> entities) {
    return entities.stream().map(TestResponseMapper::buildTestResponse).toList();
  }
}
