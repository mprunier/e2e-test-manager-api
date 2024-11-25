package fr.plum.e2e.manager.core.infrastructure.secondary.external.cypress.extractor.dto;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import fr.plum.e2e.manager.core.domain.model.aggregate.testresult.TestResultStatus;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class MochaReportTestInternal {

  private String title;
  private Integer duration;
  private Boolean pass;
  private Boolean fail;
  private Boolean pending;
  private Boolean skipped;
  private String context;
  private String code;
  private MochaReportErrorInternal err;

  public List<MochaReportContextInternal> getContextParse() throws JsonProcessingException {
    var resultList = new ArrayList<MochaReportContextInternal>();

    if (StringUtils.isNotBlank(context)) {
      var objectMapper = new ObjectMapper();
      try {
        var singleResult = objectMapper.readValue(context, MochaReportContextInternal.class);
        resultList.add(singleResult);
      } catch (MismatchedInputException e) {
        resultList = objectMapper.readValue(context, new TypeReference<>() {});
      }
    }

    return resultList;
  }

  public TestResultStatus status() {
    if (Boolean.TRUE.equals(pass)) {
      return TestResultStatus.SUCCESS;
    }
    if (Boolean.TRUE.equals(fail)) {
      return TestResultStatus.FAILED;
    }
    if (Boolean.TRUE.equals(pending) || Boolean.TRUE.equals(skipped)) {
      return TestResultStatus.SKIPPED;
    }
    return TestResultStatus.UNKNOWN;
  }
}
