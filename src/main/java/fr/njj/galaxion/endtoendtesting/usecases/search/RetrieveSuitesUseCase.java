package fr.njj.galaxion.endtoendtesting.usecases.search;

import static fr.njj.galaxion.endtoendtesting.mapper.ConfigurationSuiteResponseMapper.buildTitles;

import fr.njj.galaxion.endtoendtesting.domain.response.ConfigurationSuiteResponse;
import fr.njj.galaxion.endtoendtesting.service.retrieval.ConfigurationSuiteRetrievalService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApplicationScoped
@RequiredArgsConstructor
public class RetrieveSuitesUseCase {

  private final ConfigurationSuiteRetrievalService configurationSuiteRetrievalService;

  @Transactional
  public List<ConfigurationSuiteResponse> execute(long environmentId) {
    var configurationSuites = configurationSuiteRetrievalService.getAllByEnvironment(environmentId);
    return buildTitles(configurationSuites);
  }
}