package fr.njj.galaxion.endtoendtesting.model.search;

import fr.njj.galaxion.endtoendtesting.domain.request.SearchConfigurationRequest;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;

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

        if (request.getStatus() != null) {
            conditions.add("status = :status");
            params.put("status", request.getStatus());
        }

        query.append(String.join(" AND ", conditions));
        query.append(" ORDER BY title desc");

        return query.toString();
    }

}
