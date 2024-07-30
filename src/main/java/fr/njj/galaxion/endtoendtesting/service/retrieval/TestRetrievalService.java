package fr.njj.galaxion.endtoendtesting.service.retrieval;

import fr.njj.galaxion.endtoendtesting.domain.exception.TestNotFoundException;
import fr.njj.galaxion.endtoendtesting.domain.exception.TestVideoNotFoundException;
import fr.njj.galaxion.endtoendtesting.domain.response.ScreenshotResponse;
import fr.njj.galaxion.endtoendtesting.domain.response.TestResponse;
import fr.njj.galaxion.endtoendtesting.model.entity.TestEntity;
import fr.njj.galaxion.endtoendtesting.model.repository.TestRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

import static fr.njj.galaxion.endtoendtesting.mapper.TestResponseMapper.buildScreenshotResponses;
import static fr.njj.galaxion.endtoendtesting.mapper.TestResponseMapper.buildTestResponseWithDetails;
import static fr.njj.galaxion.endtoendtesting.mapper.TestResponseMapper.buildTestResponses;

@Slf4j
@ApplicationScoped
@RequiredArgsConstructor
public class TestRetrievalService {

    private final TestRepository testRepository;

    @Transactional
    public TestEntity get(Long id) {
        return testRepository.findByIdOptional(id)
                             .orElseThrow(() -> new TestNotFoundException(id));
    }

    @Transactional
    public byte[] getVideo(Long id) {
        var video = get(id).getVideo();
        if (video == null) {
            throw new TestVideoNotFoundException();
        }
        return video;
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
    public List<TestResponse> getErrorResponses(String pipelineId) {
        var tests = testRepository.findAllErrorByPipelineId(pipelineId);
        return buildTestResponses(tests);
    }

    @Transactional
    public List<TestEntity> getAll(List<Long> ids) {
        return testRepository.findAllBy(ids);
    }
}
