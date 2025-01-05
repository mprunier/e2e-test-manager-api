package fr.plum.e2e.manager.core.infrastructure.secondary.external.webclient.gitlab.mapper;

import static fr.plum.e2e.manager.core.domain.constant.BusinessConstant.DISABLE_TAG;
import static fr.plum.e2e.manager.core.infrastructure.secondary.external.webclient.cypress.constant.CypressConstant.START_PATH;

import fr.plum.e2e.manager.core.domain.model.aggregate.environment.Environment;
import fr.plum.e2e.manager.core.domain.model.aggregate.testconfiguration.vo.Tag;
import fr.plum.e2e.manager.core.domain.model.aggregate.worker.vo.WorkerIsRecordVideo;
import fr.plum.e2e.manager.core.domain.model.aggregate.worker.vo.WorkerUnitFilter;
import fr.plum.e2e.manager.core.domain.model.aggregate.worker.vo.WorkerVariable;
import fr.plum.e2e.manager.core.infrastructure.secondary.external.webclient.gitlab.domain.request.PipelineRequest;
import fr.plum.e2e.manager.core.infrastructure.secondary.external.webclient.gitlab.domain.request.VariableRequest;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class GitlabPipelineRequestMapper {

  public static PipelineRequest buildPipelineRequest(
      Environment environment,
      WorkerUnitFilter workerUnitFilter,
      List<WorkerVariable> workerVariables,
      WorkerIsRecordVideo workerIsRecordVideo) {
    var pipelineRequestBuilder =
        PipelineRequest.builder()
            .variable(buildCypressTestEnabledVariable())
            .ref(environment.getSourceCodeInformation().branch());

    if (!workerUnitFilter.fileNames().isEmpty()) {
      pipelineRequestBuilder.variable(
          buildCypressTestSpecVariable(
              workerUnitFilter.fileNames().stream()
                  .map(fileName -> START_PATH + fileName.value())
                  .collect(Collectors.joining(","))));
    }

    if (workerUnitFilter.suiteFilter() != null || workerUnitFilter.testFilter() != null) {
      var grep = buildGrep(workerUnitFilter);
      pipelineRequestBuilder.variable(buildCypressTestGrepVariable(grep));
    }

    var grepTags = buildGrepTags(workerUnitFilter.tag());
    pipelineRequestBuilder.variable(buildCypressTestGrepTagsVariable(grepTags));

    var cypressVariables = buildCypressVariable(environment, workerVariables);
    if (cypressVariables != null) {
      pipelineRequestBuilder.variable(buildCypressVariablesVariable(cypressVariables));
    }

    pipelineRequestBuilder.variable(buildCypressVideoVariable(workerIsRecordVideo.value()));
    return pipelineRequestBuilder.build();
  }

  private static String buildCypressVariable(
      Environment environment, List<WorkerVariable> workerVariables) {
    String cypressVariables = null;
    if (!workerVariables.isEmpty()) {
      cypressVariables =
          workerVariables.stream()
              .map(variable -> variable.name() + "=" + variable.value())
              .collect(Collectors.joining(","));
    }
    if (!environment.getVariables().isEmpty()) {
      if (cypressVariables != null) {
        cypressVariables += ",";
      } else {
        cypressVariables = "";
      }
      cypressVariables +=
          environment.getVariables().stream()
              .map(variable -> variable.getId().name() + "=" + variable.getValue().value())
              .collect(Collectors.joining(","));
    }
    return cypressVariables;
  }

  private static String buildGrep(WorkerUnitFilter workerUnitFilter) {
    var grep = new StringBuilder();
    if (workerUnitFilter.suiteFilter() != null) {
      grep.append(workerUnitFilter.suiteFilter().suiteTitle().value());
    }
    if (workerUnitFilter.testFilter() != null) {
      if (!grep.isEmpty()) {
        grep.append(" ");
      }
      grep.append(workerUnitFilter.testFilter().testTitle().value());
    }
    return grep.toString();
  }

  private static String buildGrepTags(Tag tag) {
    if (tag == null) {
      return "-" + DISABLE_TAG; // "-" =  Grep Cypress function for not run tagged tests.
    }
    return tag.value() + "+-" + DISABLE_TAG;
  }

  private static VariableRequest buildCypressTestEnabledVariable() {
    return VariableRequest.builder().key("CYPRESS_TEST_ENABLED").value("true").build();
  }

  private static VariableRequest buildCypressTestSpecVariable(String spec) {
    return VariableRequest.builder().key("CYPRESS_TEST_SPEC").value(spec).build();
  }

  private static VariableRequest buildCypressTestGrepVariable(String grep) {
    return VariableRequest.builder().key("CYPRESS_TEST_GREP").value(grep).build();
  }

  private static VariableRequest buildCypressTestGrepTagsVariable(String grepTags) {
    return VariableRequest.builder().key("CYPRESS_TEST_GREP_TAGS").value(grepTags).build();
  }

  private static VariableRequest buildCypressVariablesVariable(String variables) {
    return VariableRequest.builder().key("CYPRESS_VARIABLES").value(variables).build();
  }

  private static VariableRequest buildCypressVideoVariable(boolean isVideo) {
    return VariableRequest.builder().key("CYPRESS_VIDEO").value(isVideo ? "true" : "false").build();
  }
}
