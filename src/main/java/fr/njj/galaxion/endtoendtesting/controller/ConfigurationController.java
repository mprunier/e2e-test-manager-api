package fr.njj.galaxion.endtoendtesting.controller;

import fr.njj.galaxion.endtoendtesting.domain.request.SearchConfigurationRequest;
import fr.njj.galaxion.endtoendtesting.domain.response.ConfigurationSuiteResponse;
import fr.njj.galaxion.endtoendtesting.domain.response.ConfigurationTestResponse;
import fr.njj.galaxion.endtoendtesting.domain.response.SearchConfigurationSuiteResponse;
import fr.njj.galaxion.endtoendtesting.service.retrieval.ConfigurationTestIdentifierRetrievalService;
import fr.njj.galaxion.endtoendtesting.service.retrieval.ConfigurationTestRetrievalService;
import fr.njj.galaxion.endtoendtesting.usecases.search.SearchSuiteOrTestUseCase;
import io.quarkus.cache.CacheResult;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.BeanParam;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.QueryParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Set;

@Slf4j
@Path("/configurations")
@RequiredArgsConstructor
public class ConfigurationController {

    private final SearchSuiteOrTestUseCase searchSuiteOrTestUseCase;
    private final ConfigurationTestRetrievalService configurationTestRetrievalService;
    private final ConfigurationTestIdentifierRetrievalService configurationTestIdentifierRetrievalService;

    @GET
    @Path("/search/suites")
    public SearchConfigurationSuiteResponse searchBySuite(@NotNull @QueryParam("environmentId") Long environmentId,
                                                          @Valid @BeanParam SearchConfigurationRequest request) {
        return searchSuiteOrTestUseCase.execute(environmentId, request);
    }

    @GET
    @Path("/suites")
    @CacheResult(cacheName = "suites")
    public List<ConfigurationSuiteResponse> getConfigurationSuites(@NotNull @QueryParam("environmentId") Long environmentId) {
        return searchSuiteOrTestUseCase.getResponses(environmentId);
    }

    @GET
    @Path("/tests")
    @CacheResult(cacheName = "tests")
    public List<ConfigurationTestResponse> getConfigurationTests(@NotNull @QueryParam("environmentId") Long environmentId) {
        return configurationTestRetrievalService.getResponses(environmentId);
    }

    @GET
    @Path("/files")
    @CacheResult(cacheName = "files")
    public List<String> getConfigurationFiles(@NotNull @QueryParam("environmentId") Long environmentId) {
        return searchSuiteOrTestUseCase.getAllFiles(environmentId);
    }

    @GET
    @Path("/identifiers")
    @CacheResult(cacheName = "identifiers")
    public Set<String> getConfigurationIdentifiers(@NotNull @QueryParam("environmentId") Long environmentId) {
        return configurationTestIdentifierRetrievalService.getAllIdentifier(environmentId);
    }
}

