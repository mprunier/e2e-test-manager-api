package fr.njj.galaxion.endtoendtesting.domain.event;

import com.fasterxml.jackson.annotation.JsonTypeName;
import fr.njj.galaxion.endtoendtesting.domain.response.MetricsResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Setter
@Getter
@SuperBuilder
@AllArgsConstructor
@JsonTypeName("UPDATE_FINAL_METRICS_EVENT")
public class UpdateFinalMetricsEvent extends AbstractEvent {

    private MetricsResponse metrics;

    private boolean isAllTestsRun;
}
