package fr.njj.galaxion.endtoendtesting.model.search;

import fr.njj.galaxion.endtoendtesting.domain.enumeration.ConfigurationStatus;
import fr.njj.galaxion.endtoendtesting.domain.request.SearchConfigurationRequest;
import java.util.List;
import java.util.Map;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ConfigurationSuiteSearch {

  public static String buildConfigurationSuiteSearchQuery(
      Long environmentId,
      SearchConfigurationRequest request,
      Map<String, Object> params,
      List<String> conditions) {

    conditions.add("environment.id = :environmentId");
    params.put("environmentId", environmentId);
    StringBuilder query = new StringBuilder();

    if (request.getConfigurationSuiteId() != null) {
      conditions.add("id = :configurationSuiteId");
      params.put("configurationSuiteId", request.getConfigurationSuiteId());
    }

    if (request.getConfigurationSuiteIds() != null) {
      conditions.add("id IN :configurationSuiteIds");
      params.put("configurationSuiteIds", request.getConfigurationSuiteIds());
    }

    if (StringUtils.isNotBlank(request.getFile())) {
      conditions.add("file = :file");
      params.put("file", request.getFile());
    }

    if (Boolean.TRUE.equals(request.getAllNotSuccess())) {
      conditions.add("status != :status OR id IN :newConfigurationSuiteIds");
      params.put("status", ConfigurationStatus.SUCCESS);
      params.put("newConfigurationSuiteIds", request.getNewConfigurationSuiteIds());
    } else if (request.getStatus() != null) {
      conditions.add("status = :status");
      params.put("status", request.getStatus());
    }

    query.append(String.join(" AND ", conditions));

    String sortField =
        StringUtils.isNotBlank(request.getSortField()) ? request.getSortField() : "file";
    String sortOrder = "desc".equalsIgnoreCase(request.getSortOrder()) ? "DESC" : "ASC";
    query.append(" ORDER BY ").append(sortField).append(" ").append(sortOrder);

    return query.toString();
  }
}
