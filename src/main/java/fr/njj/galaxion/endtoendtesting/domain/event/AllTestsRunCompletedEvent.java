package fr.njj.galaxion.endtoendtesting.domain.event;

import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
@JsonTypeName("ALL_TESTS_RUN_COMPLETED_EVENT")
public class AllTestsRunCompletedEvent extends Event {

    private String lastALlTestsError;
}
