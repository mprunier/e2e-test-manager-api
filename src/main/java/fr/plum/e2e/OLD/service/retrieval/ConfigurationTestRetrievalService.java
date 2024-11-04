package fr.plum.e2e.OLD.service.retrieval;

import static fr.plum.e2e.OLD.mapper.ConfigurationTestResponseMapper.buildTitles;

import fr.plum.e2e.OLD.domain.exception.ConfigurationTestNotFoundException;
import fr.plum.e2e.OLD.domain.response.ConfigurationTestResponse;
import fr.plum.e2e.OLD.model.entity.ConfigurationSuiteEntity;
import fr.plum.e2e.OLD.model.entity.ConfigurationTestEntity;
import fr.plum.e2e.OLD.model.repository.ConfigurationTestRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApplicationScoped
@RequiredArgsConstructor
public class ConfigurationTestRetrievalService {

  private final ConfigurationTestRepository configurationTestRepository;

  @Transactional
  public Optional<ConfigurationTestEntity> getOptional(Long id) {
    return configurationTestRepository.findByIdOptional(id);
  }

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

  @Transactional
  public List<ConfigurationTestEntity> getAllByIds(Set<Long> configurationTestIds) {
    return configurationTestRepository.findAllByIds(configurationTestIds);
  }

  @Transactional
  public List<ConfigurationTestEntity> getAllByFiles(List<String> files) {
    return configurationTestRepository.findAllByFiles(files);
  }

  @Transactional
  public List<ConfigurationTestEntity> getAllNewTests(long environmentId) {
    return configurationTestRepository.findAllNewTests(environmentId);
  }
}
