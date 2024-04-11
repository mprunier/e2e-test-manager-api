package fr.njj.galaxion.endtoendtesting.domain.event;

import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
@AllArgsConstructor
@JsonTypeName("TEST_RUN_COMPLETED_EVENT")
public class TestRunCompletedEvent extends AbstractEvent {

    private Long suiteId;

    private Long testId;
}
