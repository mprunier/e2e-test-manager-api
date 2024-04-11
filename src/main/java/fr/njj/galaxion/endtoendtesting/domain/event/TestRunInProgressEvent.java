package fr.njj.galaxion.endtoendtesting.domain.event;

import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
@JsonTypeName("TEST_RUN_IN_PROGRESS_EVENT")
public class TestRunInProgressEvent extends Event {

    private Long suiteId;

    private Long testId;
}
