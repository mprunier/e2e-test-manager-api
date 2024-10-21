package fr.njj.galaxion.endtoendtesting.domain.event;

import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
@AllArgsConstructor
@JsonTypeName("RUN_COMPLETED_EVENT")
public class RunCompletedEvent extends AbstractEvent {

  // ALL TESTS
  private boolean isAllTests;

  // SUITE OR TEST
  private Long suiteId;

  private Long testId;
}
