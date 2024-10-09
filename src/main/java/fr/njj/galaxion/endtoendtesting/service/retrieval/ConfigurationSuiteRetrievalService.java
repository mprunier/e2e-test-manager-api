package fr.njj.galaxion.endtoendtesting.service.retrieval;

import fr.njj.galaxion.endtoendtesting.domain.response.ConfigurationSuiteResponse;
import fr.njj.galaxion.endtoendtesting.mapper.ConfigurationSuiteResponseMapper;
import fr.njj.galaxion.endtoendtesting.model.entity.ConfigurationSuiteEntity;
import fr.njj.galaxion.endtoendtesting.model.repository.ConfigurationSuiteRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApplicationScoped
@RequiredArgsConstructor
public class ConfigurationSuiteRetrievalService {

  private final ConfigurationSuiteRepository configurationSuiteRepository;
  private final PipelineRetrievalService pipelineRetrievalService;
  private final ConfigurationTestRetrievalService configurationTestRetrievalService;
  private final FileGroupRetrievalService fileGroupRetrievalService;

  @Transactional
  public List<ConfigurationSuiteEntity> getAllByEnvironment(long environmentId) {
    return configurationSuiteRepository.findAllBy(environmentId);
  }

  @Transactional
  public List<String> getAllFilesByEnvironment(long environmentId) {
    return configurationSuiteRepository.findAllFilesBy(environmentId);
  }

  @Transactional
  public Set<Long> getSuiteIds(Long environmentId, Set<String> files) {
    return configurationSuiteRepository.findAllByFiles(environmentId, files);
  }

  @Transactional
  public ConfigurationSuiteResponse getConfigurationSuiteResponse(Long environmentId, Long testId) {
    var configurationTest = configurationTestRetrievalService.get(testId);
    var inProgressPipelines = pipelineRetrievalService.getInProgressPipelines(environmentId);
    var fileByGroupMap = fileGroupRetrievalService.getFileByGroupMap(environmentId);
    return ConfigurationSuiteResponseMapper.build(
        configurationTest.getConfigurationSuite(), inProgressPipelines, fileByGroupMap);
  }
}
