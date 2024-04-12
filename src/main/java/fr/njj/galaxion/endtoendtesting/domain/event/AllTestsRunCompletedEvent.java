package fr.njj.galaxion.endtoendtesting.domain.event;

import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
@AllArgsConstructor
@JsonTypeName("ALL_TESTS_RUN_COMPLETED_EVENT")
public class AllTestsRunCompletedEvent extends AbstractEvent {

    private String lastAllTestsError;
}
