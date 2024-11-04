package fr.plum.e2e.OLD.domain.event.send;

import com.fasterxml.jackson.annotation.JsonTypeName;
import fr.plum.e2e.OLD.domain.event.AbstractEvent;
import fr.plum.e2e.OLD.domain.response.ConfigurationSuiteResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
@AllArgsConstructor
@JsonTypeName("RUN_COMPLETED_EVENT")
public class RunCompletedEvent extends AbstractEvent {

  // ALL TESTS
  private Boolean isAllTests;

  // SUITE OR TEST
  private ConfigurationSuiteResponse configurationSuite;
}
