package fr.njj.galaxion.endtoendtesting.usecases.search;

import static fr.njj.galaxion.endtoendtesting.model.search.ConfigurationSuiteSearch.buildConfigurationSuiteSearchQuery;

import fr.njj.galaxion.endtoendtesting.domain.enumeration.ConfigurationStatus;
import fr.njj.galaxion.endtoendtesting.domain.internal.InProgressPipelinesInternal;
import fr.njj.galaxion.endtoendtesting.domain.request.SearchConfigurationRequest;
import fr.njj.galaxion.endtoendtesting.domain.response.SearchConfigurationSuiteResponse;
import fr.njj.galaxion.endtoendtesting.mapper.ConfigurationSuiteResponseMapper;
import fr.njj.galaxion.endtoendtesting.model.entity.ConfigurationSuiteEntity;
import fr.njj.galaxion.endtoendtesting.service.retrieval.ConfigurationSuiteRetrievalService;
import fr.njj.galaxion.endtoendtesting.service.retrieval.ConfigurationSuiteTagRetrievalService;
import fr.njj.galaxion.endtoendtesting.service.retrieval.ConfigurationTestRetrievalService;
import fr.njj.galaxion.endtoendtesting.service.retrieval.ConfigurationTestTagRetrievalService;
import fr.njj.galaxion.endtoendtesting.service.retrieval.FileGroupRetrievalService;
import fr.njj.galaxion.endtoendtesting.service.retrieval.PipelineRetrievalService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

@Slf4j
@ApplicationScoped
@RequiredArgsConstructor
public class SearchSuiteOrTestUseCase {

  private final ConfigurationTestRetrievalService configurationTestRetrievalService;
  private final ConfigurationTestTagRetrievalService configurationTestTagRetrievalService;
  private final ConfigurationSuiteTagRetrievalService configurationSuiteTagRetrievalService;
  private final FileGroupRetrievalService fileGroupRetrievalService;
  private final ConfigurationSuiteRetrievalService configurationSuiteRetrievalService;
  private final PipelineRetrievalService pipelineRetrievalService;

  @Transactional
  public SearchConfigurationSuiteResponse execute(
      Long environmentId, SearchConfigurationRequest request) {

    var params = new HashMap<String, Object>();

    var inProgressPipelines = pipelineRetrievalService.getInProgressPipelines(environmentId);

    addSuiteByTag(environmentId, request);
    addSuiteByTest(request);
    addSuiteByNewTest(environmentId, request);
    addSuiteInProgress(request, inProgressPipelines);

    // To retrieve all the configuration suites that are not successful but also the new tests
    // (Suite is not set to new if only just one new test).
    if (Boolean.TRUE.equals(request.getAllNotSuccess())) {
      var configurationTest =
          configurationTestRetrievalService.getAllNewByEnvironment(environmentId);
      request.setNewConfigurationSuiteIds(
          configurationTest.stream()
              .map(
                  configurationTestEntity ->
                      configurationTestEntity.getConfigurationSuite().getId())
              .collect(Collectors.toSet()));
    }

    var baseQuery = buildConfigurationSuiteSearchQuery(environmentId, request, params);
    var filteredQuery =
        ConfigurationSuiteEntity.find(baseQuery, params).page(request.getPage(), request.getSize());
    List<ConfigurationSuiteEntity> configurationSuites = filteredQuery.list();
    long total = filteredQuery.count();

    return new SearchConfigurationSuiteResponse(
        ConfigurationSuiteResponseMapper.builds(configurationSuites, inProgressPipelines),
        request.getPage(),
        (int) Math.ceil((double) total / request.getSize()),
        request.getSize(),
        total);
  }

  private void addSuiteByTest(SearchConfigurationRequest request) {
    if (request.getConfigurationTestId() != null) {
      var configurationTest =
          configurationTestRetrievalService.get(request.getConfigurationTestId());
      request.setConfigurationSuiteId(configurationTest.getConfigurationSuite().getId());
    }
  }

  private void addSuiteByNewTest(Long environmentId, SearchConfigurationRequest request) {
    if (ConfigurationStatus.NEW.equals(request.getStatus())) {
      request.setStatus(null);
      var newSuiteIds =
          configurationTestRetrievalService.getAllNewTests(environmentId).stream()
              .map(
                  configurationTestEntity ->
                      configurationTestEntity.getConfigurationSuite().getId())
              .collect(Collectors.toSet());
      var newConfigurationSuiteIds = request.getConfigurationSuiteIds();
      if (newConfigurationSuiteIds == null) {
        newConfigurationSuiteIds = newSuiteIds;
      } else {
        newConfigurationSuiteIds =
            newConfigurationSuiteIds.stream()
                .filter(newSuiteIds::contains)
                .collect(Collectors.toSet());
      }
      request.setConfigurationSuiteIds(newConfigurationSuiteIds);
    }
  }

  private void addSuiteByTag(Long environmentId, SearchConfigurationRequest request) {
    if (StringUtils.isNotBlank(request.getTag())) {
      var configurationSuiteIds =
          configurationTestTagRetrievalService.getSuiteIds(environmentId, request.getTag());
      configurationSuiteIds.addAll(
          configurationSuiteTagRetrievalService.getSuiteIds(environmentId, request.getTag()));
      var files = fileGroupRetrievalService.getAllFiles(environmentId, request.getTag());
      configurationSuiteIds.addAll(
          configurationSuiteRetrievalService.getSuiteIds(environmentId, files));
      request.setConfigurationSuiteIds(configurationSuiteIds);
    }
  }

  private void addSuiteInProgress(
      SearchConfigurationRequest request, InProgressPipelinesInternal inProgressPipelines) {
    if (ConfigurationStatus.IN_PROGRESS.equals(request.getStatus())) {
      request.setStatus(null);
      if (!inProgressPipelines.isAllTests()) {
        var inProgressConfigurationTestIds =
            inProgressPipelines.pipelinesByConfigurationTestId().keySet();
        var inProgressConfigurationTests =
            configurationTestRetrievalService.getAllByIds(inProgressConfigurationTestIds);
        var configurationSuiteIds =
            inProgressConfigurationTests.stream()
                .map(
                    configurationTestEntity ->
                        configurationTestEntity.getConfigurationSuite().getId())
                .collect(Collectors.toSet());
        request.setConfigurationSuiteIds(configurationSuiteIds);
      }
    }
  }
}
