package fr.njj.galaxion.endtoendtesting.service.configuration;

import fr.njj.galaxion.endtoendtesting.domain.exception.ConfigurationTestNotFoundException;
import fr.njj.galaxion.endtoendtesting.domain.request.SearchConfigurationRequest;
import fr.njj.galaxion.endtoendtesting.domain.response.ConfigurationTestResponse;
import fr.njj.galaxion.endtoendtesting.domain.response.SearchConfigurationTestResponse;
import fr.njj.galaxion.endtoendtesting.model.entity.ConfigurationSuiteEntity;
import fr.njj.galaxion.endtoendtesting.model.entity.ConfigurationTestEntity;
import fr.njj.galaxion.endtoendtesting.model.entity.EnvironmentEntity;
import fr.njj.galaxion.endtoendtesting.model.repository.ConfigurationTestRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import static fr.njj.galaxion.endtoendtesting.mapper.ConfigurationTestResponseMapper.buildTitles;
import static fr.njj.galaxion.endtoendtesting.mapper.ConfigurationTestResponseMapper.builds;
import static fr.njj.galaxion.endtoendtesting.model.search.ConfigurationTestSearch.buildConfigurationTestSearchQuery;

@Slf4j
@ApplicationScoped
@RequiredArgsConstructor
public class ConfigurationTestRetrievalService {

    private final ConfigurationTestRepository configurationTestRepository;
    private final ConfigurationTestIdentifierRetrievalService configurationTestIdentifierRetrievalService;

    @Transactional
    public SearchConfigurationTestResponse search(Long environmentId, SearchConfigurationRequest request) {

        var params = new HashMap<String, Object>();
        var conditions = new ArrayList<String>();

        if (StringUtils.isNotBlank(request.getConfigurationTestIdentifier())) {
            var configurationTestIds = configurationTestIdentifierRetrievalService.getTestId(environmentId, request.getConfigurationTestIdentifier());
            request.setConfigurationTestIds(configurationTestIds);
        }

        var baseQuery = buildConfigurationTestSearchQuery(environmentId, request, params, conditions);
        var filteredQuery = ConfigurationTestEntity.find(baseQuery, params)
                                                   .page(request.getPage(), request.getSize());
        List<ConfigurationTestEntity> configurationTests = filteredQuery.list();
        long total = filteredQuery.count();

        return new SearchConfigurationTestResponse(builds(configurationTests, true),
                                                   request.getPage(),
                                                   (int) Math.ceil((double) total / request.getSize()),
                                                   request.getSize(),
                                                   total);
    }

    @Transactional
    public ConfigurationTestEntity get(Long id) {
        return configurationTestRepository.findByIdOptional(id)
                                          .orElseThrow(() -> new ConfigurationTestNotFoundException(id));
    }

    @Transactional
    public Optional<ConfigurationTestEntity> getBy(EnvironmentEntity environment, String file, String title, ConfigurationSuiteEntity configurationSuite) {
        return configurationTestRepository.findBy(file, environment.getId(), configurationSuite.getId(), title);
    }

    @Transactional
    public List<ConfigurationTestResponse> getResponses(Long environmentId) {
        var configurationTests = configurationTestRepository.findAllBy(environmentId);
        return buildTitles(configurationTests);
    }
}

