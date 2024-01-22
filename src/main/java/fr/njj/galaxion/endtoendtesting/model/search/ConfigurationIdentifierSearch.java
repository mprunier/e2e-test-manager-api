package fr.njj.galaxion.endtoendtesting.model.search;

import fr.njj.galaxion.endtoendtesting.domain.request.SearchConfigurationRequest;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ConfigurationIdentifierSearch {

    public static String buildConfigurationIdentifierSearchQuery(
            Long environmentId,
            SearchConfigurationRequest request,
            Map<String, Object> params,
            List<String> conditions) {

        conditions.add("configurationTest.environment.id = :environmentId");
        params.put("environmentId", environmentId);
        StringBuilder query = new StringBuilder();

        if (StringUtils.isNotBlank(request.getConfigurationTestIdentifier())) {
            conditions.add("identifier = :identifier");
            params.put("identifier", request.getConfigurationTestIdentifier());
        }

        if (request.getConfigurationTestId() != null) {
            conditions.add("configurationTest.id = :configurationTestId");
            params.put("configurationTestId", request.getConfigurationTestId());
        }

        if (request.getConfigurationTestIds() != null) {
            conditions.add("configurationTest.id IN :configurationTests");
            params.put("configurationTests", request.getConfigurationTestIds());
        }

        if (StringUtils.isNotBlank(request.getFile())) {
            conditions.add("configurationTest.file = :file");
            params.put("file", request.getFile());
        }

        if (request.getStatus() != null) {
            conditions.add("configurationTest.status = :status");
            params.put("status", request.getStatus());
        }

        query.append(String.join(" AND ", conditions));
        query.append(" ORDER BY identifier desc");

        return query.toString();
    }

}
