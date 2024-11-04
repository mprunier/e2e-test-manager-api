package fr.plum.e2e.manager.core.infrastructure.secondary.notification.dto;

import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Setter
@Getter
@SuperBuilder
@AllArgsConstructor
@JsonTypeName("SYNCHRONIZATION_IS_IN_PROGRESS_EVENT")
public class SynchronizationIsInProgressNotificationEvent extends AbstractNotificationEvent {}
