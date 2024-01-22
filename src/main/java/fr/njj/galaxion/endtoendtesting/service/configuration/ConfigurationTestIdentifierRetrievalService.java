package fr.njj.galaxion.endtoendtesting.service.configuration;

import fr.njj.galaxion.endtoendtesting.domain.exception.ConfigurationTestIdentifierNotFoundException;
import fr.njj.galaxion.endtoendtesting.model.entity.ConfigurationTestIdentifierEntity;
import fr.njj.galaxion.endtoendtesting.model.repository.ConfigurationTestIdentifierRepository;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@ApplicationScoped
@RequiredArgsConstructor
public class ConfigurationTestIdentifierRetrievalService {

    private final ConfigurationTestIdentifierRepository configurationTestIdentifierRepository;

    public Set<String> getAllIdentifier(Long environmentId) {
        return configurationTestIdentifierRepository.findAllByEnv(environmentId).stream().map(ConfigurationTestIdentifierEntity::getIdentifier).collect(Collectors.toSet());
    }

    public List<ConfigurationTestIdentifierEntity> getAllByEnvAndIdentifier(Long environmentId, String identifier) {
        var configurationTestIdentifiers = configurationTestIdentifierRepository.findAllByEnvAndIdentifier(environmentId, identifier);
        if (configurationTestIdentifiers.isEmpty()) {
            throw new ConfigurationTestIdentifierNotFoundException(identifier);
        }
        return configurationTestIdentifiers;
    }

    public List<Long> getTestId(Long environmentId, String identifier) {
        var configurationTestIdentifiers = getAllByEnvAndIdentifier(environmentId, identifier);
        return configurationTestIdentifiers.stream()
                                           .map(configurationTestIdentifierEntity -> configurationTestIdentifierEntity.getConfigurationTest().getId())
                                           .toList();
    }

    public List<Long> getSuiteId(Long environmentId, String identifier) {
        var configurationTestIdentifiers = getAllByEnvAndIdentifier(environmentId, identifier);
        return configurationTestIdentifiers.stream()
                                           .map(configurationTestIdentifierEntity -> configurationTestIdentifierEntity.getConfigurationTest().getConfigurationSuite().getId())
                                           .toList();
    }
}

