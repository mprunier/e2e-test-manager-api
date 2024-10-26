package fr.njj.galaxion.endtoendtesting.domain.event.send;

import com.fasterxml.jackson.annotation.JsonTypeName;
import fr.njj.galaxion.endtoendtesting.domain.event.AbstractEvent;
import fr.njj.galaxion.endtoendtesting.domain.response.ConfigurationSuiteResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
@AllArgsConstructor
@JsonTypeName("RUN_IN_PROGRESS_EVENT")
public class RunInProgressEvent extends AbstractEvent {

  // ALL TESTS
  private Boolean isAllTests;

  // SUITE OR TEST
  private ConfigurationSuiteResponse configurationSuite;
}
