package fr.njj.galaxion.endtoendtesting.usecases.run;

import static fr.njj.galaxion.endtoendtesting.domain.constant.CommonConstant.START_PATH;
import static fr.njj.galaxion.endtoendtesting.helper.EnvironmentHelper.buildVariablesEnvironment;

import fr.njj.galaxion.endtoendtesting.client.gitlab.response.GitlabResponse;
import fr.njj.galaxion.endtoendtesting.domain.enumeration.PipelineType;
import fr.njj.galaxion.endtoendtesting.domain.event.send.RunInProgressEvent;
import fr.njj.galaxion.endtoendtesting.domain.exception.AllTestsAlreadyRunningException;
import fr.njj.galaxion.endtoendtesting.model.entity.EnvironmentEntity;
import fr.njj.galaxion.endtoendtesting.model.entity.PipelineEntity;
import fr.njj.galaxion.endtoendtesting.model.entity.PipelineGroupEntity;
import fr.njj.galaxion.endtoendtesting.service.AssertPipelineReachedService;
import fr.njj.galaxion.endtoendtesting.service.gitlab.RunGitlabJobService;
import fr.njj.galaxion.endtoendtesting.service.retrieval.EnvironmentRetrievalService;
import fr.njj.galaxion.endtoendtesting.service.retrieval.FileGroupRetrievalService;
import fr.njj.galaxion.endtoendtesting.service.retrieval.PipelineRetrievalService;
import io.quarkus.cache.CacheManager;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Event;
import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApplicationScoped
@RequiredArgsConstructor
public class RunAllTestsUseCase {

  private final AssertPipelineReachedService assertPipelineReachedService;
  private final EnvironmentRetrievalService environmentRetrievalService;
  private final RunGitlabJobService runGitlabJobService;
  private final FileGroupRetrievalService fileGroupRetrievalService;
  private final PipelineRetrievalService pipelineRetrievalService;

  private final Event<RunInProgressEvent> allTestsRunInProgressEvent;

  private final CacheManager cacheManager;

  @Transactional
  public void execute(Long environmentId, String createdBy) {
    log.info("[{}] ran all tests on Environment id [{}].", createdBy, environmentId);

    assertPipelineReachedService.assertPipeline();

    var environment = environmentRetrievalService.get(environmentId);
    assertSchedulerInProgress(environment);

    if (environment.getMaxParallelTestNumber() > 1) {
      runWithMultiPipelines(environment, createdBy);
    } else {
      var pipelineGroup = createPipelineGroup(environment, 1);
      runWithOnePipeline(environment, pipelineGroup, createdBy);
    }

    allTestsRunInProgressEvent.fire(
        RunInProgressEvent.builder().isAllTests(true).environmentId(environmentId).build());
    cacheManager
        .getCache("in_progress_pipelines")
        .ifPresent(cache -> cache.invalidateAll().await().indefinitely());
  }

  private void runWithOnePipeline(
      EnvironmentEntity environment, PipelineGroupEntity pipelineGroup, String createdBy) {
    var variablesBuilder = new StringBuilder();
    buildVariablesEnvironment(environment.getVariables(), variablesBuilder);
    var gitlabResponse =
        runGitlabJobService.runJob(
            environment.getBranch(),
            environment.getToken(),
            environment.getProjectId(),
            null,
            variablesBuilder.toString(),
            null,
            null,
            false);

    createPipeline(gitlabResponse, environment, pipelineGroup, PipelineType.ALL, null, createdBy);
  }

  private void runWithMultiPipelines(EnvironmentEntity environment, String createdBy) {
    int pipelineCount = environment.getMaxParallelTestNumber();
    var filesByGroup = fileGroupRetrievalService.getAllFilesByGroup(environment.getId());
    var allFiles = environment.getFiles();

    var pipelines = initializePipelines(pipelineCount);
    var ungroupedFiles = getUngroupedFiles(allFiles, filesByGroup);

    distributeGroupedFiles(pipelines, filesByGroup);
    distributeUngroupedFiles(pipelines, ungroupedFiles);

    int actualPipelineCount = (int) pipelines.stream().filter(p -> !p.isEmpty()).count();

    var pipelineGroup = createPipelineGroup(environment, actualPipelineCount);

    if (actualPipelineCount > 1) {
      executePipelines(pipelines, environment, pipelineGroup, createdBy);
    } else {
      runWithOnePipeline(environment, pipelineGroup, createdBy);
    }
  }

  private static PipelineGroupEntity createPipelineGroup(
      EnvironmentEntity environment, int actualPipelineCount) {
    var pipelineGroup =
        PipelineGroupEntity.builder()
            .environment(environment)
            .totalPipelines(actualPipelineCount)
            .build();
    pipelineGroup.persist();
    return pipelineGroup;
  }

  private List<List<String>> initializePipelines(int pipelineCount) {
    return IntStream.range(0, pipelineCount)
        .mapToObj(i -> new ArrayList<String>())
        .collect(Collectors.toList());
  }

  private List<String> getUngroupedFiles(
      Set<String> allFiles, Map<String, List<String>> filesByGroup) {
    var groupedFiles =
        filesByGroup.values().stream().flatMap(List::stream).collect(Collectors.toSet());
    return allFiles.stream()
        .filter(file -> !groupedFiles.contains(file))
        .collect(Collectors.toList());
  }

  private void distributeGroupedFiles(
      List<List<String>> pipelines, Map<String, List<String>> filesByGroup) {
    filesByGroup.values().stream()
        .sorted((a, b) -> Integer.compare(b.size(), a.size()))
        .forEach(group -> getSmallestPipeline(pipelines).addAll(group));
  }

  private void distributeUngroupedFiles(List<List<String>> pipelines, List<String> ungroupedFiles) {
    ungroupedFiles.forEach(file -> getSmallestPipeline(pipelines).add(file));
  }

  private List<String> getSmallestPipeline(List<List<String>> pipelines) {
    return pipelines.stream()
        .min(Comparator.comparingInt(List::size))
        .orElseThrow(() -> new IllegalStateException("No pipelines available"));
  }

  private void executePipelines(
      List<List<String>> pipelines,
      EnvironmentEntity environment,
      PipelineGroupEntity pipelineGroup,
      String createdBy) {
    pipelines.stream()
        .filter(pipelineFiles -> !pipelineFiles.isEmpty())
        .forEach(
            pipelineFiles -> {
              var filesStr =
                  pipelineFiles.stream()
                      .sorted()
                      .map(file -> START_PATH + file)
                      .collect(Collectors.joining(","));
              var variablesBuilder = new StringBuilder();
              buildVariablesEnvironment(environment.getVariables(), variablesBuilder);
              var gitlabResponse =
                  runGitlabJobService.runJob(
                      environment.getBranch(),
                      environment.getToken(),
                      environment.getProjectId(),
                      filesStr,
                      variablesBuilder.toString(),
                      null,
                      null,
                      false);

              createPipeline(
                  gitlabResponse,
                  environment,
                  pipelineGroup,
                  PipelineType.ALL_IN_PARALLEL,
                  pipelineFiles,
                  createdBy);
            });
  }

  private static void createPipeline(
      GitlabResponse gitlabResponse,
      EnvironmentEntity environment,
      PipelineGroupEntity pipelineGroup,
      PipelineType pipelineType,
      List<String> files,
      String createdBy) {
    PipelineEntity.builder()
        .id(gitlabResponse.getId())
        .type(pipelineType)
        .environment(environment)
        .pipelineGroup(pipelineGroup)
        .filesFilter(files)
        .createdBy(createdBy)
        .build()
        .persist();
  }

  private void assertSchedulerInProgress(EnvironmentEntity environment) {
    if (pipelineRetrievalService.isAllTestRunning(environment.getId())) {
      throw new AllTestsAlreadyRunningException();
    }
  }
}
