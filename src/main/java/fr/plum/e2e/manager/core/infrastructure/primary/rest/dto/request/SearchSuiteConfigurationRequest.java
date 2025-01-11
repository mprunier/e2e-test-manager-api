package fr.plum.e2e.manager.core.infrastructure.primary.rest.dto.request;

import fr.plum.e2e.manager.core.domain.model.aggregate.environment.vo.EnvironmentId;
import fr.plum.e2e.manager.core.domain.model.aggregate.testconfiguration.ConfigurationStatus;
import fr.plum.e2e.manager.core.domain.model.aggregate.testconfiguration.vo.FileName;
import fr.plum.e2e.manager.core.domain.model.aggregate.testconfiguration.vo.SuiteConfigurationId;
import fr.plum.e2e.manager.core.domain.model.aggregate.testconfiguration.vo.Tag;
import fr.plum.e2e.manager.core.domain.model.aggregate.testconfiguration.vo.TestConfigurationId;
import fr.plum.e2e.manager.core.domain.model.query.SearchSuiteConfigurationQuery;
import fr.plum.e2e.manager.core.infrastructure.primary.rest.dto.enumeration.SearchSuiteConfigurationSortField;
import fr.plum.e2e.manager.core.infrastructure.primary.rest.dto.enumeration.SearchSuiteConfigurationSortOrder;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.QueryParam;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class SearchSuiteConfigurationRequest {

  @QueryParam("configurationSuiteId")
  private UUID configurationSuiteId;

  @QueryParam("configurationTestId")
  private UUID configurationTestId;

  @QueryParam("tag")
  private String tag;

  @QueryParam("file")
  private String file;

  @QueryParam("status")
  private ConfigurationStatus status;

  @QueryParam("allNotSuccess")
  private Boolean allNotSuccess;

  @QueryParam("page")
  @DefaultValue("0")
  private int page;

  @QueryParam("size")
  @DefaultValue("10")
  private int size;

  @QueryParam("sortField")
  @DefaultValue("file")
  private SearchSuiteConfigurationSortField sortField;

  @QueryParam("sortOrder")
  @DefaultValue("desc")
  private SearchSuiteConfigurationSortOrder sortOrder;

  public SearchSuiteConfigurationQuery toQuery(UUID environmentId) {
    return SearchSuiteConfigurationQuery.builder()
        .environmentId(new EnvironmentId(environmentId))
        .suiteConfigurationId(
            configurationSuiteId != null ? new SuiteConfigurationId(configurationSuiteId) : null)
        .testConfigurationId(
            configurationTestId != null ? new TestConfigurationId(configurationTestId) : null)
        .tag(tag != null ? new Tag(tag) : null)
        .fileName(file != null ? new FileName(file) : null)
        .status(status)
        .allNotSuccess(allNotSuccess != null ? allNotSuccess : false)
        .page(page)
        .size(size)
        .sortField(sortField.getField())
        .sortOrder(sortOrder.getOrder())
        .build();
  }
}
