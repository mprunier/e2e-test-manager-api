package fr.njj.galaxion.endtoendtesting.service.retrieval;

import fr.njj.galaxion.endtoendtesting.model.entity.ConfigurationSuiteEntity;
import fr.njj.galaxion.endtoendtesting.model.repository.ConfigurationSuiteRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApplicationScoped
@RequiredArgsConstructor
public class ConfigurationSuiteRetrievalService {

  private final ConfigurationSuiteRepository configurationSuiteRepository;

  @Transactional
  public List<ConfigurationSuiteEntity> getAllByEnvironment(long environmentId) {
    return configurationSuiteRepository.findAllBy(environmentId);
  }

  @Transactional
  public List<String> getAllFilesByEnvironment(long environmentId) {
    return configurationSuiteRepository.findAllFilesBy(environmentId);
  }
}
