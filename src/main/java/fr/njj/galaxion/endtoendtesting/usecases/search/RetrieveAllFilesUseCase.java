package fr.njj.galaxion.endtoendtesting.usecases.search;

import fr.njj.galaxion.endtoendtesting.service.retrieval.ConfigurationSuiteRetrievalService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApplicationScoped
@RequiredArgsConstructor
public class RetrieveAllFilesUseCase {

  private final ConfigurationSuiteRetrievalService configurationSuiteRetrievalService;

  @Transactional
  public List<String> execute(long environmentId) {
    return configurationSuiteRetrievalService.getAllFilesByEnvironment(environmentId);
  }
}
