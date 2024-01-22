package fr.njj.galaxion.endtoendtesting.mapper;

import fr.njj.galaxion.endtoendtesting.domain.response.EnvironmentVariableResponse;
import fr.njj.galaxion.endtoendtesting.model.entity.EnvironmentVariableEntity;
import java.util.List;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class EnvironmentVariableResponseMapper {

  private static EnvironmentVariableResponse buildEnvironmentVariableResponse(
      EnvironmentVariableEntity entity) {
    return EnvironmentVariableResponse.builder()
        .id(entity.getId())
        .description(entity.getDescription())
        .name(entity.getName())
        .defaultValue(
            Boolean.TRUE.equals(entity.getIsHidden()) ? hideVariable() : entity.getDefaultValue())
        .isHidden(entity.getIsHidden())
        .build();
  }

  public static List<EnvironmentVariableResponse> buildEnvironmentVariableResponses(
      List<EnvironmentVariableEntity> entities) {
    if (entities == null) return null;
    return entities.stream()
        .map(EnvironmentVariableResponseMapper::buildEnvironmentVariableResponse)
        .toList();
  }

  private static String hideVariable() {
    return "**********";
  }
}
