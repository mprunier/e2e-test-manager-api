package fr.plum.e2e.manager.core.infrastructure.secondary.persistence.jpa.entity.worker;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class WorkerUnitFilterDto {
  private List<String> fileNames;
  private String tag;
  private FilterSuiteDto suiteFilter;
  private FilterTestDto testFilter;

  @Data
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public static class FilterSuiteDto {
    private UUID suiteConfigurationId;
    private String suiteTitle;
  }

  @Data
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public static class FilterTestDto {
    private UUID testConfigurationId;
    private String testTitle;
  }
}
