package fr.njj.galaxion.endtoendtesting.service.retrieval;

import static fr.njj.galaxion.endtoendtesting.mapper.ConfigurationTestResponseMapper.buildTitles;

import fr.njj.galaxion.endtoendtesting.domain.exception.ConfigurationTestNotFoundException;
import fr.njj.galaxion.endtoendtesting.domain.response.ConfigurationTestResponse;
import fr.njj.galaxion.endtoendtesting.model.entity.ConfigurationSuiteEntity;
import fr.njj.galaxion.endtoendtesting.model.entity.ConfigurationTestEntity;
import fr.njj.galaxion.endtoendtesting.model.repository.ConfigurationTestRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApplicationScoped
@RequiredArgsConstructor
public class ConfigurationTestRetrievalService {

  private final ConfigurationTestRepository configurationTestRepository;

  @Transactional
  public ConfigurationTestEntity get(Long id) {
    return configurationTestRepository
        .findByIdOptional(id)
        .orElseThrow(() -> new ConfigurationTestNotFoundException(id));
  }

  @Transactional
  public List<ConfigurationTestEntity> getAllNewByEnvironment(Long environmentId) {
    return configurationTestRepository.findAllNewByEnvironment(environmentId);
  }

  @Transactional
  public Optional<ConfigurationTestEntity> getBy(
      long environmentId, String file, String title, ConfigurationSuiteEntity configurationSuite) {
    return configurationTestRepository.findBy(
        file, environmentId, configurationSuite.getId(), title);
  }

  @Transactional
  public List<ConfigurationTestResponse> getResponses(Long environmentId) {
    var configurationTests = configurationTestRepository.findAllBy(environmentId);
    return buildTitles(configurationTests);
  }
}
