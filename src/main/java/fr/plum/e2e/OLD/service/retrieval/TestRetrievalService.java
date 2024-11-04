package fr.plum.e2e.OLD.service.retrieval;

import static fr.plum.e2e.OLD.mapper.TestResponseMapper.buildScreenshotResponses;
import static fr.plum.e2e.OLD.mapper.TestResponseMapper.buildTestResponseWithDetails;
import static fr.plum.e2e.OLD.mapper.TestResponseMapper.buildTestResponses;

import fr.plum.e2e.OLD.domain.exception.TestNotFoundException;
import fr.plum.e2e.OLD.domain.exception.TestVideoNotFoundException;
import fr.plum.e2e.OLD.domain.response.ScreenshotResponse;
import fr.plum.e2e.OLD.domain.response.TestResponse;
import fr.plum.e2e.OLD.model.entity.TestEntity;
import fr.plum.e2e.OLD.model.repository.TestRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApplicationScoped
@RequiredArgsConstructor
public class TestRetrievalService {

  private final TestRepository testRepository;

  @Transactional
  public TestEntity get(Long id) {
    return testRepository.findByIdOptional(id).orElseThrow(() -> new TestNotFoundException(id));
  }

  @Transactional
  public byte[] getVideo(Long id) {
    var video = get(id).getVideo();
    if (video == null) {
      throw new TestVideoNotFoundException();
    }
    return video.getVideo();
  }

  @Transactional
  public List<ScreenshotResponse> getScreenshots(Long id) {
    return buildScreenshotResponses(get(id));
  }

  @Transactional
  public TestResponse getResponse(Long id) {
    var test = get(id);
    return buildTestResponseWithDetails(test);
  }

  @Transactional
  public List<TestResponse> getResponses(Long configurationTestId) {
    var tests = testRepository.findAllByConfigurationTestId(configurationTestId);
    return buildTestResponses(tests);
  }

  @Transactional
  public List<TestEntity> getAll(List<Long> ids) {
    return testRepository.findAllBy(ids);
  }
}
