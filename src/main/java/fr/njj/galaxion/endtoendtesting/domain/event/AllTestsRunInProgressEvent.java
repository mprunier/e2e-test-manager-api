package fr.njj.galaxion.endtoendtesting.domain.event;

import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
@AllArgsConstructor
@JsonTypeName("ALL_TESTS_RUN_IN_PROGRESS_EVENT")
public class AllTestsRunInProgressEvent extends AbstractEvent {}
