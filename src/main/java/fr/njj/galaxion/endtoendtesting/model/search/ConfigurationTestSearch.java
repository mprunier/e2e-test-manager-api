package fr.njj.galaxion.endtoendtesting.model.search;

import fr.njj.galaxion.endtoendtesting.domain.request.SearchConfigurationRequest;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ConfigurationTestSearch {

    public static String buildConfigurationTestSearchQuery(
            Long environmentId,
            SearchConfigurationRequest request,
            Map<String, Object> params,
            List<String> conditions) {

        conditions.add("environment.id = :environmentId");
        params.put("environmentId", environmentId);
        StringBuilder query = new StringBuilder();

        if (request.getConfigurationSuiteId() != null) {
            conditions.add("configurationSuite.id = :configurationSuiteId");
            params.put("configurationSuiteId", request.getConfigurationSuiteId());
        }

        if (request.getConfigurationTestId() != null) {
            conditions.add("id = :configurationTestId");
            params.put("configurationTestId", request.getConfigurationTestId());
        }

        if (request.getConfigurationTestIds() != null) {
            conditions.add("id IN :configurationTestIds");
            params.put("configurationTestIds", request.getConfigurationTestIds());
        }

        if (StringUtils.isNotBlank(request.getFile())) {
            conditions.add("file = :file");
            params.put("file", request.getFile());
        }

        if (request.getStatus() != null) {
            conditions.add("status = :status");
            params.put("status", request.getStatus());
        }

        query.append(String.join(" AND ", conditions));
        query.append(" ORDER BY configurationSuite.title, id asc");

        return query.toString();
    }

}
