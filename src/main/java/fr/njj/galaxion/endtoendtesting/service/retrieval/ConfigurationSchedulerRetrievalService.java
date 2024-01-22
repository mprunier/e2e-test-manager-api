package fr.njj.galaxion.endtoendtesting.service.retrieval;

import fr.njj.galaxion.endtoendtesting.model.entity.ConfigurationSchedulerEntity;
import fr.njj.galaxion.endtoendtesting.model.repository.ConfigurationSchedulerRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApplicationScoped
@RequiredArgsConstructor
public class ConfigurationSchedulerRetrievalService {

  private final ConfigurationSchedulerRepository configurationSchedulerRepository;

  @Transactional
  public ConfigurationSchedulerEntity getByEnvironment(long environmentId) {
    return configurationSchedulerRepository.findBy(environmentId);
  }
}
