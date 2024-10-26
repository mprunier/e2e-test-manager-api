package fr.njj.galaxion.endtoendtesting.service;

import fr.njj.galaxion.endtoendtesting.model.entity.TemporaryTestEntity;
import fr.njj.galaxion.endtoendtesting.model.entity.TestEntity;
import fr.njj.galaxion.endtoendtesting.model.repository.TemporaryTestRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@ApplicationScoped
@RequiredArgsConstructor
public class TestTransformationService {

  private final TemporaryTestRepository temporaryTestRepository;

  @Transactional
  public void transformAndPersistAll(String pipelineId) {
    var temporaryTests = temporaryTestRepository.findAllByPipelineId(pipelineId);
    temporaryTests.forEach(this::transformAndPersist);
  }

  private void transformAndPersist(TemporaryTestEntity temporaryTest) {
    var test =
        TestEntity.builder()
            .configurationTest(temporaryTest.getConfigurationTest())
            .variables(temporaryTest.getVariables())
            .status(temporaryTest.getStatus())
            .reference(temporaryTest.getReference())
            .errorUrl(temporaryTest.getErrorUrl())
            .errorMessage(temporaryTest.getErrorMessage())
            .errorStacktrace(temporaryTest.getErrorStacktrace())
            .code(temporaryTest.getCode())
            .duration(temporaryTest.getDuration())
            .video(temporaryTest.getVideo())
            .screenshots(temporaryTest.getScreenshots())
            .createdAt(temporaryTest.getCreatedAt())
            .createdBy(temporaryTest.getCreatedBy())
            .build();

    if (test.getScreenshots() != null) {
      test.getScreenshots()
          .forEach(
              screenshot -> {
                screenshot.setTest(test);
                screenshot.setTemporaryTest(null);
              });
    }

    test.persist();
    temporaryTest.delete();
  }
}
