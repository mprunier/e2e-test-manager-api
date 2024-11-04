package fr.plum.e2e.OLD.service.retrieval;

import fr.plum.e2e.OLD.model.entity.ConfigurationTestTagEntity;
import fr.plum.e2e.OLD.model.repository.ConfigurationTestTagRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApplicationScoped
@RequiredArgsConstructor
public class ConfigurationTestTagRetrievalService {

  private final ConfigurationTestTagRepository configurationTestTagRepository;

  @Transactional
  public Set<String> getAllTags(Long environmentId) {
    return configurationTestTagRepository.findAllByEnv(environmentId).stream()
        .map(ConfigurationTestTagEntity::getTag)
        .collect(Collectors.toSet());
  }

  @Transactional
  public Set<Long> getSuiteIds(Long environmentId, String tag) {
    var configurationTestTags = getAllByEnvAndTag(environmentId, tag);
    return configurationTestTags.stream()
        .map(
            configurationTestTagEntity ->
                configurationTestTagEntity.getConfigurationTest().getConfigurationSuite().getId())
        .collect(Collectors.toSet());
  }

  private List<ConfigurationTestTagEntity> getAllByEnvAndTag(Long environmentId, String tag) {
    var configurationTestTags =
        configurationTestTagRepository.findAllByEnvAndTag(environmentId, tag);
    if (configurationTestTags.isEmpty()) {
      return List.of();
    }
    return configurationTestTags;
  }
}
