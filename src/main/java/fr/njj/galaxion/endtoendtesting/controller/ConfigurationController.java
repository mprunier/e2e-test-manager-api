package fr.njj.galaxion.endtoendtesting.controller;

import fr.njj.galaxion.endtoendtesting.domain.request.SearchConfigurationRequest;
import fr.njj.galaxion.endtoendtesting.domain.response.ConfigurationSuiteResponse;
import fr.njj.galaxion.endtoendtesting.domain.response.ConfigurationTestResponse;
import fr.njj.galaxion.endtoendtesting.domain.response.SearchConfigurationSuiteResponse;
import fr.njj.galaxion.endtoendtesting.service.retrieval.ConfigurationSuiteTagRetrievalService;
import fr.njj.galaxion.endtoendtesting.service.retrieval.ConfigurationTestRetrievalService;
import fr.njj.galaxion.endtoendtesting.service.retrieval.ConfigurationTestTagRetrievalService;
import fr.njj.galaxion.endtoendtesting.service.retrieval.FileGroupRetrievalService;
import fr.njj.galaxion.endtoendtesting.usecases.search.RetrieveAllFilesUseCase;
import fr.njj.galaxion.endtoendtesting.usecases.search.RetrieveSuitesUseCase;
import fr.njj.galaxion.endtoendtesting.usecases.search.SearchSuiteOrTestUseCase;
import io.quarkus.cache.CacheResult;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.BeanParam;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.QueryParam;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Path("/configurations")
@RequiredArgsConstructor
public class ConfigurationController {

  private final SearchSuiteOrTestUseCase searchSuiteOrTestUseCase;
  private final ConfigurationTestRetrievalService configurationTestRetrievalService;
  private final ConfigurationTestTagRetrievalService configurationTestTagRetrievalService;
  private final ConfigurationSuiteTagRetrievalService configurationSuiteTagRetrievalService;
  private final RetrieveAllFilesUseCase retrieveAllFilesUseCase;
  private final RetrieveSuitesUseCase retrieveSuitesUseCase;
  private final FileGroupRetrievalService fileGroupRetrievalService;

  @GET
  @Path("/search/suites")
  public SearchConfigurationSuiteResponse searchBySuite(
      @NotNull @QueryParam("environmentId") Long environmentId,
      @Valid @BeanParam SearchConfigurationRequest request) {
    return searchSuiteOrTestUseCase.execute(environmentId, request);
  }

  @GET
  @Path("/suites")
  @CacheResult(cacheName = "suites")
  public List<ConfigurationSuiteResponse> getConfigurationSuites(
      @NotNull @QueryParam("environmentId") Long environmentId) {
    return retrieveSuitesUseCase.execute(environmentId);
  }

  @GET
  @Path("/tests")
  @CacheResult(cacheName = "tests")
  public List<ConfigurationTestResponse> getConfigurationTests(
      @NotNull @QueryParam("environmentId") Long environmentId) {
    return configurationTestRetrievalService.getResponses(environmentId);
  }

  @GET
  @Path("/files")
  @CacheResult(cacheName = "files")
  public List<String> getConfigurationFiles(
      @NotNull @QueryParam("environmentId") Long environmentId) {
    return retrieveAllFilesUseCase.execute(environmentId);
  }

  @GET
  @Path("/tags")
  @CacheResult(cacheName = "tags")
  public Set<String> getConfigurationTags(
      @NotNull @QueryParam("environmentId") Long environmentId) {
    var tags = configurationSuiteTagRetrievalService.getAllTags(environmentId);
    tags.addAll(configurationTestTagRetrievalService.getAllTags(environmentId));
    tags.addAll(fileGroupRetrievalService.getAllGroups(environmentId));
    return tags;
  }
}
