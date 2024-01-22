package fr.njj.galaxion.endtoendtesting.domain.internal;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
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
  private String fullTitle;
  private Integer timedOut;
  private Integer duration;
  private String state;
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
}
