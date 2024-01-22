package fr.njj.galaxion.endtoendtesting.service.retrieval;

import fr.njj.galaxion.endtoendtesting.model.entity.ConfigurationSuiteTagEntity;
import fr.njj.galaxion.endtoendtesting.model.repository.ConfigurationSuiteTagRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApplicationScoped
@RequiredArgsConstructor
public class ConfigurationSuiteTagRetrievalService {

  private final ConfigurationSuiteTagRepository configurationSuiteTagRepository;

  @Transactional
  public Set<String> getAllTags(Long environmentId) {
    return configurationSuiteTagRepository.findAllByEnv(environmentId).stream()
        .map(ConfigurationSuiteTagEntity::getTag)
        .collect(Collectors.toSet());
  }

  @Transactional
  public Set<Long> getSuiteIds(Long environmentId, String tag) {
    var configurationTestTags =
        configurationSuiteTagRepository.findAllByEnvAndTag(environmentId, tag);
    if (configurationTestTags.isEmpty()) {
      return Set.of();
    }
    return configurationTestTags.stream()
        .map(
            configurationSuiteTagEntity ->
                configurationSuiteTagEntity.getConfigurationSuite().getId())
        .collect(Collectors.toSet());
  }
}
