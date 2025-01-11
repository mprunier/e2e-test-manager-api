package fr.plum.e2e.manager.core.infrastructure.secondary.websocket.dto;

import com.fasterxml.jackson.annotation.JsonTypeName;
import fr.plum.e2e.manager.core.infrastructure.primary.rest.dto.response.MetricsResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Setter
@Getter
@SuperBuilder
@AllArgsConstructor
@JsonTypeName("UPDATE_FINAL_METRICS_EVENT")
public class UpdateFinalMetricsNotificationsEvent extends AbstractNotificationEvent {

  private MetricsResponse metrics;
}
