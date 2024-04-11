package fr.njj.galaxion.endtoendtesting.domain.event;

import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
@JsonTypeName("TEST_RUN_COMPLETED_EVENT")
public class TestRunCompletedEvent extends Event {

    private Long suiteId;

    private Long testId;
}
