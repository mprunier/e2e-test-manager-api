package fr.plum.e2e.OLD.domain.event.send;

import com.fasterxml.jackson.annotation.JsonTypeName;
import fr.plum.e2e.OLD.domain.event.AbstractEvent;
import fr.plum.e2e.OLD.domain.response.MetricsResponse;
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

  private Boolean isAllTestsRun;
}
