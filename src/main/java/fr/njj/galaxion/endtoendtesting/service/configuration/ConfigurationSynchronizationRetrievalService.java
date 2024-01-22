package fr.njj.galaxion.endtoendtesting.service.configuration;

import fr.njj.galaxion.endtoendtesting.domain.response.ConfigurationSynchronizationResponse;
import fr.njj.galaxion.endtoendtesting.model.entity.ConfigurationSynchronizationEntity;
import fr.njj.galaxion.endtoendtesting.model.repository.ConfigurationSynchronizationRepository;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import static fr.njj.galaxion.endtoendtesting.mapper.ConfigurationSynchronizationMapper.build;

@Slf4j
@ApplicationScoped
@RequiredArgsConstructor
public class ConfigurationSynchronizationRetrievalService {

    private final ConfigurationSynchronizationRepository configurationSynchronizationRepository;

    public ConfigurationSynchronizationEntity get(Long environmentId) {
        return configurationSynchronizationRepository.findBy(environmentId);
    }

    public ConfigurationSynchronizationResponse getResponse(Long environmentId) {
        return build(get(environmentId));
    }
}

