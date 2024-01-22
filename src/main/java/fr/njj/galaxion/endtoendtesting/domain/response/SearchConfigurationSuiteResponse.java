package fr.njj.galaxion.endtoendtesting.domain.response;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class SearchConfigurationSuiteResponse {

  private List<ConfigurationSuiteResponse> content;
  private Integer currentPage;
  private Integer totalPages;
  private Integer size;

  private Long totalElements;
}
