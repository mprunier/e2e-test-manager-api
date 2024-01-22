package fr.njj.galaxion.endtoendtesting.usecases.search;

import static fr.njj.galaxion.endtoendtesting.model.search.ConfigurationSuiteSearch.buildConfigurationSuiteSearchQuery;

import fr.njj.galaxion.endtoendtesting.domain.request.SearchConfigurationRequest;
import fr.njj.galaxion.endtoendtesting.domain.response.SearchConfigurationSuiteResponse;
import fr.njj.galaxion.endtoendtesting.mapper.ConfigurationSuiteResponseMapper;
import fr.njj.galaxion.endtoendtesting.model.entity.ConfigurationSuiteEntity;
import fr.njj.galaxion.endtoendtesting.service.retrieval.ConfigurationSuiteTagRetrievalService;
import fr.njj.galaxion.endtoendtesting.service.retrieval.ConfigurationTestRetrievalService;
import fr.njj.galaxion.endtoendtesting.service.retrieval.ConfigurationTestTagRetrievalService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import java.util.ArrayList;
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

  @Transactional
  public SearchConfigurationSuiteResponse execute(
      Long environmentId, SearchConfigurationRequest request) {

    var params = new HashMap<String, Object>();
    var conditions = new ArrayList<String>();

    if (StringUtils.isNotBlank(request.getTag())) {
      var configurationSuiteIds =
          configurationTestTagRetrievalService.getSuiteIds(environmentId, request.getTag());
      configurationSuiteIds.addAll(
          configurationSuiteTagRetrievalService.getSuiteIds(environmentId, request.getTag()));
      request.setConfigurationSuiteIds(configurationSuiteIds);
    }

    if (request.getConfigurationTestId() != null) {
      var configurationTest =
          configurationTestRetrievalService.get(request.getConfigurationTestId());
      request.setConfigurationSuiteId(configurationTest.getConfigurationSuite().getId());
    }

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

    var baseQuery = buildConfigurationSuiteSearchQuery(environmentId, request, params, conditions);
    var filteredQuery =
        ConfigurationSuiteEntity.find(baseQuery, params).page(request.getPage(), request.getSize());
    List<ConfigurationSuiteEntity> configurationSuites = filteredQuery.list();
    long total = filteredQuery.count();

    return new SearchConfigurationSuiteResponse(
        ConfigurationSuiteResponseMapper.builds(configurationSuites),
        request.getPage(),
        (int) Math.ceil((double) total / request.getSize()),
        request.getSize(),
        total);
  }
}
