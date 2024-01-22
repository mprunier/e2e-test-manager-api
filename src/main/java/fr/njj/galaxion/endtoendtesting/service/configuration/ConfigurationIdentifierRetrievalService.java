package fr.njj.galaxion.endtoendtesting.service.configuration;

import fr.njj.galaxion.endtoendtesting.domain.request.SearchConfigurationRequest;
import fr.njj.galaxion.endtoendtesting.domain.response.SearchConfigurationIdentifierResponse;
import fr.njj.galaxion.endtoendtesting.mapper.ConfigurationIdentifierResponseMapper;
import fr.njj.galaxion.endtoendtesting.model.entity.ConfigurationTestIdentifierEntity;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static fr.njj.galaxion.endtoendtesting.model.search.ConfigurationIdentifierSearch.buildConfigurationIdentifierSearchQuery;

@Slf4j
@ApplicationScoped
@RequiredArgsConstructor
public class ConfigurationIdentifierRetrievalService {

    private final ConfigurationSuiteRetrievalService configurationSuiteRetrievalService;

    public SearchConfigurationIdentifierResponse search(
            Long environmentId,
            SearchConfigurationRequest request) {

        var params = new HashMap<String, Object>();
        var conditions = new ArrayList<String>();

        if (request.getConfigurationSuiteId() != null) {
            var configurationTestIds = configurationSuiteRetrievalService.getTestsIds(request.getConfigurationSuiteId());
            request.setConfigurationTestIds(configurationTestIds);
        }

        var baseQuery = buildConfigurationIdentifierSearchQuery(environmentId, request, params, conditions);
        var filteredQuery = ConfigurationTestIdentifierEntity
                .find(baseQuery, params)
                .page(request.getPage(), request.getSize());
        List<ConfigurationTestIdentifierEntity> configurationSuites = filteredQuery.list();
        long total = filteredQuery.count();

        return new SearchConfigurationIdentifierResponse(ConfigurationIdentifierResponseMapper.builds(configurationSuites),
                                                         request.getPage(),
                                                         (int) Math.ceil((double) total / request.getSize()),
                                                         request.getSize(),
                                                         total);
    }
}

