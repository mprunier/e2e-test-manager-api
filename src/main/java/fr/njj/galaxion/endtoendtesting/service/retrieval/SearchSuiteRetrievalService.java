package fr.njj.galaxion.endtoendtesting.service.retrieval;

import fr.njj.galaxion.endtoendtesting.domain.exception.ConfigurationSuiteNotFoundException;
import fr.njj.galaxion.endtoendtesting.domain.response.ConfigurationSuiteResponse;
import fr.njj.galaxion.endtoendtesting.model.entity.ConfigurationSuiteEntity;
import fr.njj.galaxion.endtoendtesting.model.repository.ConfigurationSuiteRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Optional;

import static fr.njj.galaxion.endtoendtesting.mapper.ConfigurationSuiteResponseMapper.buildTitles;

@Slf4j
@ApplicationScoped
@RequiredArgsConstructor
public class SearchSuiteRetrievalService {

    private final ConfigurationSuiteRepository configurationSuiteRepository;

    @Transactional
    public ConfigurationSuiteEntity get(Long id) {
        return configurationSuiteRepository.findByIdOptional(id)
                                           .orElseThrow(() -> new ConfigurationSuiteNotFoundException(id));
    }

    @Transactional
    public Optional<ConfigurationSuiteEntity> getBy(long environmentId, String file, String title, Long parentSuiteId) {
        return configurationSuiteRepository.findBy(file,
                                                   environmentId,
                                                   title,
                                                   parentSuiteId);
    }

    @Transactional
    public List<ConfigurationSuiteResponse> getResponses(long environmentId) {
        var configurationSuites = getAllBy(environmentId);
        return buildTitles(configurationSuites);
    }

    @Transactional
    public List<ConfigurationSuiteEntity> getAllBy(long environmentId) {
        return configurationSuiteRepository.findAllBy(environmentId);
    }

    @Transactional
    public List<String> getAllFiles(long environmentId) {
        return configurationSuiteRepository.findAllFilesBy(environmentId);
    }
}

