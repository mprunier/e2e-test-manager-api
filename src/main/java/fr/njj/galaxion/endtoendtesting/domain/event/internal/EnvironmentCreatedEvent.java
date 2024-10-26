package fr.njj.galaxion.endtoendtesting.domain.event.internal;

import com.fasterxml.jackson.annotation.JsonTypeName;
import fr.njj.galaxion.endtoendtesting.domain.event.AbstractEvent;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Setter
@Getter
@SuperBuilder
@AllArgsConstructor
@JsonTypeName("ENVIRONMENT_CRATED_EVENT")
public class EnvironmentCreatedEvent extends AbstractEvent {}
