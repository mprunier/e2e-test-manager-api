package fr.plum.e2e.manager.core.infrastructure.secondary.jpa.entity.synchronization;

import java.io.Serializable;
import java.time.ZonedDateTime;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class JpaSynchronizationErrorId implements Serializable {
  private JpaSynchronizationEntity synchronization;
  private String file;
  private String error;
  private ZonedDateTime at;
}
