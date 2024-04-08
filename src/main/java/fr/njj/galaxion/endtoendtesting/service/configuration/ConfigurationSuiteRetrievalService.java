package fr.njj.galaxion.endtoendtesting.service.configuration;

import fr.njj.galaxion.endtoendtesting.domain.exception.ConfigurationSuiteNotFoundException;
import fr.njj.galaxion.endtoendtesting.domain.request.SearchConfigurationRequest;
import fr.njj.galaxion.endtoendtesting.domain.response.ConfigurationSuiteResponse;
import fr.njj.galaxion.endtoendtesting.domain.response.SearchConfigurationSuiteResponse;
import fr.njj.galaxion.endtoendtesting.mapper.ConfigurationSuiteResponseMapper;
import fr.njj.galaxion.endtoendtesting.model.entity.ConfigurationSuiteEntity;
import fr.njj.galaxion.endtoendtesting.model.entity.ConfigurationTestEntity;
import fr.njj.galaxion.endtoendtesting.model.repository.ConfigurationSuiteRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import static fr.njj.galaxion.endtoendtesting.mapper.ConfigurationSuiteResponseMapper.buildTitles;
import static fr.njj.galaxion.endtoendtesting.model.search.ConfigurationSuiteSearch.buildConfigurationSuiteSearchQuery;

@Slf4j
@ApplicationScoped
@RequiredArgsConstructor
public class ConfigurationSuiteRetrievalService {

    private final ConfigurationSuiteRepository configurationSuiteRepository;
    private final ConfigurationTestRetrievalService configurationTestRetrievalService;
    private final ConfigurationTestIdentifierRetrievalService configurationTestIdentifierRetrievalService;

    @Transactional
    public SearchConfigurationSuiteResponse search(Long environmentId, SearchConfigurationRequest request) {

        var params = new HashMap<String, Object>();
        var conditions = new ArrayList<String>();

        if (StringUtils.isNotBlank(request.getConfigurationTestIdentifier())) {
            var configurationTestIds = configurationTestIdentifierRetrievalService.getSuiteId(environmentId, request.getConfigurationTestIdentifier());
            request.setConfigurationSuiteIds(configurationTestIds);
        }

        if (request.getConfigurationTestId() != null) {
            var configurationTest = configurationTestRetrievalService.get(request.getConfigurationTestId());
            request.setConfigurationSuiteId(configurationTest.getConfigurationSuite().getId());
        }

        var baseQuery = buildConfigurationSuiteSearchQuery(environmentId, request, params, conditions);
        var filteredQuery = ConfigurationSuiteEntity.find(baseQuery, params)
                                                    .page(request.getPage(), request.getSize());
        List<ConfigurationSuiteEntity> configurationSuites = filteredQuery.list();
        long total = filteredQuery.count();

        return new SearchConfigurationSuiteResponse(ConfigurationSuiteResponseMapper.builds(configurationSuites),
                                                    request.getPage(),
                                                    (int) Math.ceil((double) total / request.getSize()),
                                                    request.getSize(),
                                                    total);
    }

    @Transactional
    public ConfigurationSuiteEntity get(Long id) {
        return configurationSuiteRepository.findByIdOptional(id)
                                           .orElseThrow(() -> new ConfigurationSuiteNotFoundException(id));
    }

    @Transactional
    public List<Long> getTestsIds(Long id) {
        var suiteOptional = configurationSuiteRepository.findByIdOptional(id);
        return suiteOptional
                .map(configurationSuiteEntity -> configurationSuiteEntity
                        .getConfigurationTests()
                        .stream()
                        .map(ConfigurationTestEntity::getId)
                        .toList())
                .orElse(Collections.emptyList());
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

