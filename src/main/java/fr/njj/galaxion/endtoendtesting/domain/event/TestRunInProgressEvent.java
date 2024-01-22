package fr.njj.galaxion.endtoendtesting.domain.event;

import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
@AllArgsConstructor
@JsonTypeName("TEST_RUN_IN_PROGRESS_EVENT")
public class TestRunInProgressEvent extends AbstractEvent {

  private Long suiteId;

  private Long testId;
}
