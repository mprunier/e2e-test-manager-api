package fr.njj.galaxion.endtoendtesting.service.configuration;

import fr.njj.galaxion.endtoendtesting.domain.request.UpdateConfigurationSchedulerRequest;
import fr.njj.galaxion.endtoendtesting.domain.response.ConfigurationSchedulerResponse;
import fr.njj.galaxion.endtoendtesting.model.entity.ConfigurationSchedulerEntity;
import fr.njj.galaxion.endtoendtesting.model.entity.EnvironmentEntity;
import fr.njj.galaxion.endtoendtesting.model.repository.ConfigurationSchedulerRepository;
import io.quarkus.cache.CacheResult;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
@ApplicationScoped
@RequiredArgsConstructor
public class ConfigurationSchedulerService {

    private final ConfigurationSchedulerRepository configurationSchedulerRepository;

    @Transactional
    public void create(EnvironmentEntity environment) {
        ConfigurationSchedulerEntity.builder()
                                    .environment(environment)
                                    .build()
                                    .persist();
    }

    @Transactional
    public void update(Long environmentId, UpdateConfigurationSchedulerRequest request) {
        var configurationScheduler = configurationSchedulerRepository.findBy(environmentId);
        configurationScheduler.setEnabled(request.getIsEnabled());
        configurationScheduler.setScheduledTime(request.getScheduledTime());
        configurationScheduler.setDaysOfWeek(request.getDaysOfWeek());
    }

    @Transactional
    public ConfigurationSchedulerResponse get(Long environmentId) {
        var configurationScheduler = configurationSchedulerRepository.findBy(environmentId);
        return ConfigurationSchedulerResponse.builder()
                                             .isEnabled(configurationScheduler.isEnabled())
                                             .scheduledTime(configurationScheduler.getScheduledTime())
                                             .daysOfWeek(configurationScheduler.getDaysOfWeek())
                                             .build();
    }

    @Transactional
    @CacheResult(cacheName = "schedulers")
    public List<ConfigurationSchedulerEntity> getAllEnabled() {
        return configurationSchedulerRepository.findAllEnabled();
    }
}

