package fr.njj.galaxion.endtoendtesting.mapper;

import static fr.njj.galaxion.endtoendtesting.mapper.EnvironmentVariableResponseMapper.buildEnvironmentVariableResponses;

import fr.njj.galaxion.endtoendtesting.domain.response.EnvironmentResponse;
import fr.njj.galaxion.endtoendtesting.model.entity.EnvironmentEntity;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class EnvironmentResponseMapper {

  public static EnvironmentResponse buildEnvironmentResponse(
      EnvironmentEntity entity, Boolean withDetails) {
    var envBuilder =
        EnvironmentResponse.builder().id(entity.getId()).description(entity.getDescription());

    if (Boolean.TRUE.equals(withDetails)) {
      var variables = buildEnvironmentVariableResponses(entity.getVariables());
      envBuilder
          .projectId(entity.getProjectId())
          .token(hideToken(entity.getToken()))
          .branch(entity.getBranch())
          .isEnabled(entity.getIsEnabled())
          .isLocked(entity.getIsLocked())
          .isRunningAllTests(entity.getIsRunningAllTests())
          .lastAllTestsError(entity.getLastALlTestsError())
          .variables(variables)
          .createdAt(entity.getCreatedAt())
          .updatedAt(entity.getUpdatedAt())
          .createdBy(entity.getCreatedBy())
          .updatedBy(entity.getUpdatedBy());
    }
    return envBuilder.build();
  }

  private static String hideToken(String token) {
    if (token == null || token.length() <= 6) {
      return "**********";
    }

    StringBuilder tokenTransforme = new StringBuilder(token);
    for (int i = 3; i < token.length() - 3; i++) {
      tokenTransforme.setCharAt(i, '*');
    }

    return tokenTransforme.toString();
  }
}
