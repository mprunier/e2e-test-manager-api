package fr.njj.galaxion.endtoendtesting.domain.event;

import com.fasterxml.jackson.annotation.JsonTypeName;
import fr.njj.galaxion.endtoendtesting.domain.response.EnvironmentResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
@JsonTypeName("UPDATE_ENVIRONMENT_EVENT")
public class UpdateEnvironmentEvent extends Event {

    private EnvironmentResponse environment;
}
