package fr.njj.galaxion.endtoendtesting.controller;

import fr.njj.galaxion.endtoendtesting.domain.request.SearchConfigurationRequest;
import fr.njj.galaxion.endtoendtesting.domain.response.ConfigurationSuiteResponse;
import fr.njj.galaxion.endtoendtesting.domain.response.ConfigurationTestResponse;
import fr.njj.galaxion.endtoendtesting.domain.response.SearchConfigurationIdentifierResponse;
import fr.njj.galaxion.endtoendtesting.domain.response.SearchConfigurationSuiteResponse;
import fr.njj.galaxion.endtoendtesting.domain.response.SearchConfigurationTestResponse;
import fr.njj.galaxion.endtoendtesting.service.configuration.ConfigurationIdentifierRetrievalService;
import fr.njj.galaxion.endtoendtesting.service.configuration.ConfigurationSuiteRetrievalService;
import fr.njj.galaxion.endtoendtesting.service.configuration.ConfigurationTestIdentifierRetrievalService;
import fr.njj.galaxion.endtoendtesting.service.configuration.ConfigurationTestRetrievalService;
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

    private final ConfigurationSuiteRetrievalService configurationSuiteRetrievalService;
    private final ConfigurationTestRetrievalService configurationTestRetrievalService;
    private final ConfigurationIdentifierRetrievalService configurationIdentifierRetrievalService;
    private final ConfigurationTestIdentifierRetrievalService configurationTestIdentifierRetrievalService;

    @GET
    @Path("/search/tests")
    public SearchConfigurationTestResponse searchByTest(@NotNull @QueryParam("environmentId") Long environmentId,
                                                        @Valid @BeanParam SearchConfigurationRequest request) {
        return configurationTestRetrievalService.search(environmentId, request);
    }

    @GET
    @Path("/search/suites")
    public SearchConfigurationSuiteResponse searchBySuite(@NotNull @QueryParam("environmentId") Long environmentId,
                                                          @Valid @BeanParam SearchConfigurationRequest request) {
        return configurationSuiteRetrievalService.search(environmentId, request);
    }

    @GET
    @Path("/search/identifiers")
    public SearchConfigurationIdentifierResponse searchByIdentifier(@NotNull @QueryParam("environmentId") Long environmentId,
                                                                    @Valid @BeanParam SearchConfigurationRequest request) {
        return configurationIdentifierRetrievalService.search(environmentId, request);
    }

    @GET
    @Path("/suites")
    @CacheResult(cacheName = "suites")
    public List<ConfigurationSuiteResponse> getConfigurationSuites(@NotNull @QueryParam("environmentId") Long environmentId) {
        return configurationSuiteRetrievalService.getResponses(environmentId);
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
        return configurationSuiteRetrievalService.getAllFiles(environmentId);
    }

    @GET
    @Path("/identifiers")
    @CacheResult(cacheName = "identifiers")
    public Set<String> getConfigurationIdentifiers(@NotNull @QueryParam("environmentId") Long environmentId) {
        return configurationTestIdentifierRetrievalService.getAllIdentifier(environmentId);
    }
}

