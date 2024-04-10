package fr.njj.galaxion.endtoendtesting.domain.event;

import com.fasterxml.jackson.annotation.JsonTypeName;
import fr.njj.galaxion.endtoendtesting.domain.response.SyncErrorResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@JsonTypeName("SYNC_ERRORS_EVENT")
public class SyncErrorsEvent extends Event {

    private List<SyncErrorResponse> syncErrors;
}
