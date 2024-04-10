package fr.njj.galaxion.endtoendtesting.domain.event;

import com.fasterxml.jackson.annotation.JsonTypeName;
import fr.njj.galaxion.endtoendtesting.domain.response.MetricsResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
@JsonTypeName("UPDATE_FINAL_METRICS_EVENT")
public class UpdateFinalMetricsEvent extends Event {

    private MetricsResponse metrics;
}
