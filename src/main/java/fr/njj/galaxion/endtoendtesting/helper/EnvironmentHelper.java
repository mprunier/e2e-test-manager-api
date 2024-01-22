package fr.njj.galaxion.endtoendtesting.helper;

import fr.njj.galaxion.endtoendtesting.model.entity.EnvironmentVariableEntity;
import java.util.List;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class EnvironmentHelper {

  public static void buildVariablesEnvironment(
      List<EnvironmentVariableEntity> environmentVariableEntities, StringBuilder variablesBuilder) {
    if (StringUtils.isNotBlank(variablesBuilder.toString())) {
      variablesBuilder.append(",");
    }
    environmentVariableEntities.forEach(
        variable ->
            variablesBuilder
                .append(variable.getName())
                .append("=")
                .append(variable.getDefaultValue())
                .append(","));
    if (!environmentVariableEntities.isEmpty()) {
      variablesBuilder.deleteCharAt(variablesBuilder.length() - 1);
    }
  }
}
