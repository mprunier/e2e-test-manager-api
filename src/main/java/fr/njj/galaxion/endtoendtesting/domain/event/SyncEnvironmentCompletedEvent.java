package fr.njj.galaxion.endtoendtesting.domain.event;

import com.fasterxml.jackson.annotation.JsonTypeName;
import fr.njj.galaxion.endtoendtesting.domain.response.EnvironmentResponse;
import fr.njj.galaxion.endtoendtesting.domain.response.SyncErrorResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Setter
@Getter
@SuperBuilder
@AllArgsConstructor
@JsonTypeName("SYNC_ENVIRONMENT_COMPLETED_EVENT")
public class SyncEnvironmentCompletedEvent extends AbstractEvent {

    private EnvironmentResponse environment;
    private List<SyncErrorResponse> syncErrors;
}